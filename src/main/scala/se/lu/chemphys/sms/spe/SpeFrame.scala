package se.lu.chemphys.sms.spe
import se.lu.chemphys.sms.movies.Frame

import java.awt.image.BufferedImage

class SpeFrame (val XDim: Int, val YDim: Int, private val data: Array[Int]) {
	
	assert(data.length == XDim * YDim, "Wrong length of frame data array, must be XDim*YDim.")
	assert(XDim > 0 && YDim > 0, "Frame dimensions must be positive")
	
	val sum = data.sum
	val min = data.min
	val max = data.max
	val average: Double = sum.toDouble / data.length
	
	def apply(i: Int, j: Int): Int = {
		if(i < 0 || j < 0 || i >= XDim || j >= YDim) throw new IndexOutOfBoundsException("Index out of bounds in Frame(x,y) call.")
		data(j*XDim + i)
	}
	
	def getImage() : BufferedImage = {
		val image = new BufferedImage(XDim, YDim, BufferedImage.TYPE_3BYTE_BGR)
		val array = new Array[Int](XDim * YDim)
		var i = 0
		while(i < array.length){
			val ints = 255 * (data(i) - min) / (max - min)
			array(i) = ints + (ints << 8) + (ints << 16)
			i = i + 1
		}
		image.setRGB(0, 0, XDim, YDim, array, 0, XDim)
		image
	}
}

class ShortFrame(XDim: Int, YDim: Int, shorts: Array[Byte]) extends SpeFrame(XDim, YDim, SpeFrame.shortBytesToInts(shorts))
class IntFrame(XDim: Int, YDim: Int, ints: Array[Byte]) extends SpeFrame(XDim, YDim, SpeFrame.intBytesToInts(ints))

object SpeFrame{
	
	def shortBytesToInts(shorts: Array[Byte]): Array[Int] = {
		val array = new Array[Int](shorts.length / 2)
		var i = 0;
		val imax = array.length
		while(i < imax){
			array(i) = (shorts(i*2) & 0x00ff) | (shorts(i*2+1) << 8)
			i = i + 1
		}
		array
	}

	def shortBytesToShorts(bytes: Array[Byte]): Array[Short] = {
		val array = new Array[Short](bytes.length / 2)
		var i = 0;
		val imax = array.length
		while(i < imax){
			array(i) = ((bytes(i*2) & 0x00ff) | (bytes(i*2+1) << 8)).toShort
			i = i + 1
		}
		array
	}

	def intBytesToInts(ints: Array[Byte]): Array[Int] = {
		val array = new Array[Int](ints.length / 4)
		var i = 0;
		val imax = array.length
		while(i < imax){
			array(i) = ints(i*4) & 0x000000ff | ints(i*4+1) << 8 & 0x0000ff00 | ints(i*4+2) << 16 & 0x00ff0000 | ints(i*4+3) << 24 & 0xff000000
			i = i + 1
		}
		array
	}
}