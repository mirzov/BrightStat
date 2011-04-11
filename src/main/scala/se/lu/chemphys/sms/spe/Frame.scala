package se.lu.chemphys.sms.spe

import java.awt.image.BufferedImage

class Frame[T](val XDim: Int, val YDim: Int, protected val data: Array[T])(implicit num: Numeric[T]) {
	import num._
	val min: T = data.min
	val max: T = data.max
	lazy val average: Double = data.map(_.toDouble).sum / XDim / YDim
	def withinRange(i: Int, j: Int): Boolean = (i >= 0 && j >= 0 && i < XDim && j < YDim)
	def apply(i: Int, j: Int): T = {
		assert(withinRange(i, j), "Index is out of range in call to Frame(i, j)")
		data(j*XDim + i)
	}
	
	def update(i: Int, j: Int, x: T){
		assert(withinRange(i, j), "Index is out of range in call to Frame(i, j)=")
		data(j*XDim + i) = x
	}
	
	def isLocalMax(i: Int, j: Int): Boolean = {
		i > 0 && j > 0 && i < XDim - 1 && j < YDim - 1  && {
			var i0 = -1;
			while((i0 <= 1)) {
				var j0 = -1;
				while((j0 <= 1)){
					if (((i0 | j0) != 0) && (this(i, j) <= this(i + i0, j + j0))) return false;
					j0 = j0 + 1
				}
				i0 = i0 + 1
			}
			true
		}
	}
	
	def getBrightCluster(i: Int, j: Int, number: Int): Set[(Int, Int)] = {
		def addNext(seed: Set[(Int, Int)]): Set[(Int, Int)] = {
			var maxInt = min
			var maxCoord: (Int, Int) = null
			for(pix <- seed){
				for(m <- (pix._1 - 1) to (pix._1 + 1); n <- (pix._2 - 1) to (pix._2 + 1)){
					val int = this(m, n)
					if(int > maxInt){
						val coords = (m, n)
						if(!seed.contains(coords)) {maxInt = int; maxCoord = coords}
					}
				}
			}
			seed + maxCoord
		}
		var res = Set((i, j))
		for(n <- 1 to (number - 1)) res = addNext(res)
		res
	}
	
	def getImage : BufferedImage = {
		val image = new BufferedImage(XDim, YDim, BufferedImage.TYPE_3BYTE_BGR)
		val array = new Array[Int](XDim * YDim)
		var i = 0
		while(i < array.length){
			val ints = ((255 * (data(i) - min).toDouble) / (max - min).toDouble).toInt
			array(i) = ints + (ints << 8) + (ints << 16)
			i = i + 1
		}
		image.setRGB(0, 0, XDim, YDim, array, 0, XDim)
		image
	}
}
