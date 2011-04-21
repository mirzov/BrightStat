package se.lu.chemphys.sms.spe

import se.lu.chemphys.sms.brightstat._
import java.awt.image.BufferedImage

class Frame[T](val XDim: Int, val YDim: Int, protected val data: Array[T])(implicit num: Numeric[T]) {
	import num._
	val min: T = data.min
	val max: T = data.max
	lazy val average: Double = data.map(_.toDouble).sum / XDim / YDim
	def withinRange(i: Int, j: Int): Boolean = (i >= 0 && j >= 0 && i < XDim && j < YDim)
	def apply(i: Int, j: Int): T = {
		if(!withinRange(i, j)) throw new IndexOutOfBoundsException("Index is out of range in call to Frame(i, j)")
		data(j * XDim + i)
	}
	
	def update(i: Int, j: Int, x: T){
		if(!withinRange(i, j)) throw new IndexOutOfBoundsException("Index is out of range in call to Frame(i, j)=")
		data(j * XDim + i) = x
	}
	
	def isLocalMax(i: Int, j: Int): Boolean = {
		i > 0 && j > 0 && i < XDim - 1 && j < YDim - 1  && {
			var i0 = -1;
			while((i0 <= 1)) {
				var j0 = -1;
				while((j0 <= 1)){
					if (((i0 | j0) != 0) && (data(j*XDim + i) <= data((j + j0)*XDim + i + i0))) return false;
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
				for(m <- (pix._1 - 1) to (pix._1 + 1); n <- (pix._2 - 1) to (pix._2 + 1) if(withinRange(m, n))){
					val int = data(n * XDim + m)
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
	
	def detectLocalMaxs(pars: PPars): Seq[(Int, Int)] = {
		val maxROI = ROI(1, 1, XDim - 2, YDim - 2)
		val roi = if(pars.UseROI) pars.roi.intersect(maxROI) else maxROI
		for(i <- roi.left to roi.right; j <- roi.top to roi.bottom if isLocalMax(i, j)) yield (i, j)
	}
	
	def detectMolecules(pars: PPars): Seq[(Int, Int)] = {
		detectLocalMaxs(pars)
			.filter(m => isCompact(m._1, m._2, pars))
			.filter(m => isSignificant(m._1, m._2, pars))
	}
	
	private val fullROI = ROI(0, 0, XDim - 1, YDim - 1)
	def isSignificant(x: Int, y: Int, pars: PPars): Boolean = {
		val desiredROI = ROI(x - pars.ImRad, y - pars.ImRad, x + pars.ImRad, y + pars.ImRad)
		val roi = fullROI.intersect(desiredROI)
		var distaver = 0d; var Iaver = 0d; var dist2aver = 0d; var I2aver = 0d; var distIaver = 0d
		var k = 0
		for(i <- roi.left to roi.right; j <- roi.top to roi.bottom){
			val dist2 = (i - x)*(i - x) + (j - y)*(j - y)
			if(dist2 <= pars.ImRad * pars.ImRad)
			{
				val I = data(j * XDim + i).toDouble
				val dist = math.sqrt(dist2)
				distaver += dist
				dist2aver += dist2
				Iaver += I
				I2aver += I*I
				distIaver += dist*I
				k += 1
			}
		}
		val PearsonR=(distIaver-distaver*Iaver/k)/
        	math.sqrt( (dist2aver-distaver*distaver/k)*(I2aver-Iaver*Iaver/k) );
		PearsonR <= - pars.Correlation
	}
	
	def isCompact(x: Int, y: Int, pars: PPars): Boolean = {
		val cluster = getBrightCluster(x, y, pars.BrightNum)
		val dist2max = pars.BrightSize * pars.BrightSize
		cluster.forall(p => (x - p._1)*(x - p._1) + (y - p._2)*(y - p._2) < dist2max)
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
