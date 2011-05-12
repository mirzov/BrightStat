package se.lu.chemphys.sms.spe

import se.lu.chemphys.sms.brightstat.MolStat
import scala.collection.mutable.ArrayBuffer
import se.lu.chemphys.sms.brightstat.PPars
import java.nio.ByteOrder
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.io.BufferedInputStream
import java.io.FileInputStream

class Movie(filePath: String) {
	private val file: FileChannel = new FileInputStream(filePath).getChannel
	
	override def finalize(){
		file.close()
	}

	val headerLength = 4100
	private val headerBytes = ByteBuffer.allocate(headerLength)
	file.read(headerBytes)
	private val header: ByteBuffer = headerBytes.order(ByteOrder.LITTLE_ENDIAN)

	private def wrongDatatypeException: Nothing = throw new RuntimeException("Unsupported SPE frame datatype: " + datatype)
	val datatype: Int = header.getShort(108).toInt
	val dataLength: Int = datatype match{
		case 0 => 4
		case 1 => 4
		case 2 => 2
		case 3 => 2
		case 6 => 1
		case _ => wrongDatatypeException 
	}
	
	val Nframes: Int = header.getInt(1446)
	val XDim: Int = header.getShort(42).toInt
	val YDim: Int = header.getShort(656).toInt

	val frameLength: Int = XDim * YDim * dataLength
	
//	def getFrame(n: Int): SpeFrame = {
//		if(n < 1 || n > Nframes) throw new IndexOutOfBoundsException("A frame number outside of the actual interval has been requested.")
//		val neededPosition = headerLength + (n - 1) * frameLength
//		if(file.position != neededPosition) file.position(neededPosition)
//		val frameBytes = ByteBuffer.allocate(frameLength)
//		file.read(frameBytes)
//		dataLength match{
//			case 2 => new ShortFrame(XDim, YDim, frameBytes.array)
//			case 4 => new IntFrame(XDim, YDim, frameBytes.array)
//			case _ => throw new RuntimeException("Only SPE datatypes coded as 2 and 4 are supported")
//		}
//	}
	
	def getFrame(n: Int): Frame[_] = {
		import Utils._
		if(n < 1 || n > Nframes) throw new IndexOutOfBoundsException("A frame number outside of the actual interval has been requested.")
		val neededPosition = headerLength + (n - 1) * frameLength
		if(file.position != neededPosition) file.position(neededPosition)
		val frameBytes = ByteBuffer.allocate(frameLength)
		file.read(frameBytes)
		datatype match{
			case 3 =>
				val arr: Array[Int] = shortBytesToInts(frameBytes.array)
				new Frame(XDim, YDim, arr)
			case 1 =>
				val arr: Array[Int] = intBytesToInts(frameBytes.array)
				new Frame(XDim, YDim, arr)
			case 2 =>
				val arr: Array[Short] = shortBytesToShorts(frameBytes.array)
				new Frame(XDim, YDim, arr)
			case 0 =>
				val arr: Array[Float] = floatBytesToFloats(frameBytes.array)
				new Frame(XDim, YDim, arr)
			case 6 =>
				val arr: Array[Short] = byteBytesToShorts(frameBytes.array)
				new Frame(XDim, YDim, arr)
			case _ => wrongDatatypeException
		}
	}
	
	def detectMoleculesFromScratch(startFrame: Int, pars: PPars): Array[MolStat] = {
		def areSame(mol1: (Int, Int), mol2: (Int, Int)): Boolean = 
			pars.withinImRange(mol1._1 - mol2._1, mol1._2 - mol2._2)
		var frame = getFrame(startFrame)
		var maxs = frame.detectLocalMaxs(pars)
		var mols = frame.detectMolecules(maxs, pars).map{pix => (pix, 0)}
		val endFrame = (startFrame + pars.NofStartFrames - 1).min(Nframes).toInt
		for(f <- (startFrame + 1) to endFrame){
			frame = getFrame(f)
			val newMols = ArrayBuffer[((Int, Int), Int)]()
			for((pix, occurence) <- mols){
				val newPix = frame.shiftToLocalMax(pix)
				if(areSame(pix, newPix) && frame.isMolecule(pix, pars)) newMols += ((newPix, occurence + 1))
				else if(occurence > 0) newMols += ((newPix, occurence))
			}
			maxs = frame.detectLocalMaxs(pars)
			val freshmols = frame.detectMolecules(maxs, pars)
			for(freshmol <- freshmols if newMols.exists(mol => areSame(mol._1, freshmol))){
				newMols += ((freshmol, 0))
			}
			mols = newMols
		}
		val molPixels = mols.filter(_._2 > 0).map(_._1)
		frame.markBrightNonMolecules(maxs, pars)
		frame.calcSignals(molPixels, pars, false)
	}
	
}