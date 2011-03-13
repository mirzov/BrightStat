package se.lu.chemphys.sms.spe

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

	val datatype: Short = header.getShort(108)
	if(datatype == 0) throw new RuntimeException("Floating point SPE-files are not supported")
	val dataLength: Short = if(datatype > 1) 2 else 4
	
	val Nframes: Int = header.getInt(1446)
	val XDim: Short = header.getShort(42)
	val YDim: Short = header.getShort(656)

	val frameLength: Int = XDim * YDim * dataLength
	
	def getFrame(n: Int): Frame = {
		if(n < 1 || n > Nframes) throw new IndexOutOfBoundsException("A frame number outside of the actual interval has been requested.")
		val neededPosition = headerLength + (n - 1) * frameLength
		if(file.position != neededPosition) file.position(neededPosition)
		val frameBytes = ByteBuffer.allocate(frameLength)
		file.read(frameBytes)
		dataLength match{
			case 2 => new ShortFrame(XDim, YDim, frameBytes.array)
			case 4 => new IntFrame(XDim, YDim, frameBytes.array)
			case _ => throw new RuntimeException("Only SPE datatypes coded as 2 and 4 are supported")
		}
	}
	
	override def finalize(){
		file.close()
	}
}