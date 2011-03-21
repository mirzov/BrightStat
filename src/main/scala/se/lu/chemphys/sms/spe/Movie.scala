package se.lu.chemphys.sms.spe

import se.lu.chemphys.sms.movies.Frame
import java.nio.ByteOrder
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.io.BufferedInputStream
import java.io.FileInputStream

class Movie(filePath: String) {
	private val file: FileChannel = new FileInputStream(filePath).getChannel
	
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
	
	override def finalize(){
		file.close()
	}
}