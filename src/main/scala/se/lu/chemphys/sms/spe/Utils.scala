package se.lu.chemphys.sms.spe

import scala.collection.mutable.ArrayBuffer
import se.lu.chemphys.sms.brightstat.MolStat
import se.lu.chemphys.sms.brightstat.PPars
object Utils {
	
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

	def byteBytesToShorts(bytes: Array[Byte]): Array[Short] = {
		val array = new Array[Short](bytes.length)
		var i = 0;
		val imax = array.length
		while(i < imax){
			array(i) = (bytes(i) & 0x00ff).toShort
			i = i + 1
		}
		array
	}

	def byteBytesToInts(bytes: Array[Byte]): Array[Int] = {
		val array = new Array[Int](bytes.length)
		var i = 0;
		val imax = array.length
		while(i < imax){
			array(i) = (bytes(i) & 0x000000ff).toInt
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
	
	def floatBytesToFloats(floats: Array[Byte]): Array[Float] = {
		val array = new Array[Float](floats.length / 4)
		var i = 0;
		val imax = array.length
		while(i < imax){
			val bits: Int = floats(i*4) & 0x000000ff | floats(i*4+1) << 8 & 0x0000ff00 | floats(i*4+2) << 16 & 0x00ff0000 | floats(i*4+3) << 24 & 0xff000000
			array(i) = java.lang.Float.intBitsToFloat(bits)
			i = i + 1
		}
		array
	}

	def getClusterSizeSq(cluster: Iterable[(Int, Int)], center: (Int, Int)): Float = {
		var sizeSq = 0f
		val (x, y) = center
		for((i, j) <- cluster){
			val dx = i - x
			val dy = j - y
			val curSizeSq = dx * dx + dy * dy;
			if(sizeSq < curSizeSq) sizeSq = curSizeSq
		}
		sizeSq
	}
	
}
