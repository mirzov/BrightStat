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
	
	val marks = new Marks(XDim, YDim)
	
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
	
	private val fullROI = ROI(0, 0, XDim - 1, YDim - 1)

	def shiftToLocalMax(pixel: (Int, Int)): (Int, Int) = {
		var max = pixel
		var oldMax: (Int, Int) = null
		while(max != oldMax){
			oldMax = max
			max = shiftToBrightestNeighbor(oldMax)
		}
		max
	}
	
	def shiftToBrightestNeighbor(pixel: (Int, Int)): (Int, Int) = {
		var (x, y) = pixel
		val desiredRoi = ROI(x - 1, y - 1, x + 1, y + 1)
		val roi = fullROI.intersect(desiredRoi)
		var iMax = Double.MinValue
		for(i <- roi.left to roi.right; j <- roi.top to roi.bottom){
			if(data(j * XDim + i).toDouble > iMax){
				iMax = data(j * XDim + i).toDouble
				x = i; y = j
			}
		}
		(x, y)
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
		for(
			i <- roi.left to roi.right;
			j <- roi.top to roi.bottom
			if isLocalMax(i, j) && marks.isEmpty(i, j) &&
			(!pars.UseExProfile || (pars.ExFrame(i, j) > pars.ExFrame.max * pars.CutOff))
		) yield (i, j)
	}
	
	def detectMolecules(maxs: Seq[(Int, Int)], pars: PPars): Seq[(Int, Int)] = {
		val mols = maxs.filter(m => isMolecule(m, pars))
		mols.foreach(m => marks.markMolecule(m._1, m._2, pars))
		mols
	}
	
	def isMolecule(m: (Int, Int), pars: PPars) = isCompact(m._1, m._2, pars) && isSignificant(m._1, m._2, pars)
	
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
	
	def calcLocalStat(x: Int, y: Int, pars: PPars): PixelStatistics = {
		val pixels = for(
			i <- (x - pars.SmRad) to (x + pars.SmRad);
			j <- (y - pars.SmRad) to (y + pars.SmRad)
			if(marks.isEmpty(i, j))
		) yield data(j * XDim + i).toDouble
		new PixelStatistics(pixels, pars)
	}
	
	def markBrightNonMolecules(maxs: Seq[(Int, Int)], pars: PPars){
		var stats: PixelStatistics = null
		def isTooBright(x: Int, y: Int) = !stats.isWithin(data(y * XDim + x).toDouble)
		maxs.foreach{m =>
			val (x, y) = m
			if(marks.isEmpty(x, y)){
				stats = calcLocalStat(x, y, pars)
				if(isTooBright(x, y)){
					for(
						i <- (x - pars.SmRad) to (x + pars.SmRad);
						j <- (y - pars.SmRad) to (y + pars.SmRad)
						if marks.isEmpty(i, j) && isTooBright(i, j) && pars.withinSmRange(i - x, j - y)
					) yield marks.makeBright(i, j)
				}
			}
		}
	}
	
	def calcSignals(pixels: Seq[(Int, Int)], pars: PPars, filter: Boolean): Seq[(Int, Int, Double)] = {
		for(
			(x, y) <- pixels;
			val stats = calcLocalStat(x, y, pars)
			if(!filter || !stats.isWithin(data(y * XDim + x).toDouble))
		) yield {
			var molSum = 0d
			var num = 0
			for(
				i <- (x - pars.ImRad) to (x + pars.ImRad);
				j <- (y - pars.ImRad) to (y + pars.ImRad);
				if(pars.withinImRange(x - i, y - j))
			) {molSum += data(y * XDim + x).toDouble; num +=1}
			(x, y, molSum - stats.mean * num)
		}
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
