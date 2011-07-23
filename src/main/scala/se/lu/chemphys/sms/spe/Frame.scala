package se.lu.chemphys.sms.spe

import se.lu.chemphys.sms.brightstat._
import java.awt.image.BufferedImage
import com.azavea.math.Numeric

class Frame[T](val XDim: Int, val YDim: Int, protected val data: Array[T])(implicit num: Numeric[T]) {
	import num._
	import Numeric._
	implicit val ordering = num.getOrdering
	val min: T = data.min
	val max: T = data.max
	lazy val average: Double = data.map(d => num.toDouble(d)).sum / XDim / YDim
	def withinRange(i: Int, j: Int): Boolean = (i >= 0 && j >= 0 && i < XDim && j < YDim)
	def apply(i: Int, j: Int): T = {
		if(!withinRange(i, j)) throw new IndexOutOfBoundsException("Index is out of range in call to Frame(i, j)")
		data(j * XDim + i)
	}
	
	private var _marks: Marks = null
	def marks = {
		if(_marks == null) _marks = new Marks(XDim, YDim)
		_marks
	}
	def resetMarks(){ if(_marks != null) _marks.reset()}
	
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
		val (x, y) = pixel
		var iMax = data(y * XDim + x)
		var xmax = x; var ymax = y;
		var i = x - 1
		while(i <= x + 1){
			var j = y - 1
			while(j <= y + 1){
				val candidate = data(j * XDim + i)
				if(candidate > iMax){
					xmax = i; ymax = j; iMax = candidate
				}
				j += 1
			}
			i += 1
		}
		(xmax, ymax)
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
		val roi = pars.getSafeRoi(XDim, YDim)
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
		val imRad = scala.math.ceil(pars.ImRad).toInt
		val desiredROI = ROI(x - imRad, y - imRad, x + imRad, y + imRad)
		val roi = fullROI.intersect(desiredROI)
		var distaver = 0d; var Iaver = 0d; var dist2aver = 0d; var I2aver = 0d; var distIaver = 0d
		var k = 0
		for(i <- roi.left to roi.right; j <- roi.top to roi.bottom){
			val dist2 = (i - x)*(i - x) + (j - y)*(j - y)
			if(dist2 <= pars.ImRad * pars.ImRad)
			{
				val I = num.toDouble(data(j * XDim + i))
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
		val clusterSizeSq = Utils.getClusterSizeSq(cluster, (x, y))
		clusterSizeSq <= pars.BrightSize * pars.BrightSize
	}
	
	def calcLocalStat(x: Int, y: Int, pars: PPars): PixelStatistics = {
		if(!withinRange(x, y)) throw new IndexOutOfBoundsException("Index is out of range in call to calcLocalStat")
		val pixels = for(
			i <- (x - pars.SmRad).toInt to (x + pars.SmRad).toInt;
			j <- (y - pars.SmRad).toInt to (y + pars.SmRad).toInt
			if(marks.isEmpty(i, j))
		) yield num.toDouble(data(j * XDim + i))
		new PixelStatistics(pixels, pars)
	}
	
	def markBrightNonMolecules(maxs: Seq[(Int, Int)], pars: PPars){
		var stats: PixelStatistics = null
		def isTooBright(x: Int, y: Int) = stats.isOutside(data(y * XDim + x))
		maxs.foreach{m =>
			val (x, y) = m
			if(marks.isEmpty(x, y)){
				stats = calcLocalStat(x, y, pars)
				if(isTooBright(x, y)){
					for(
						i <- (x - pars.SmRad).toInt to (x + pars.SmRad).toInt;
						j <- (y - pars.SmRad).toInt to (y + pars.SmRad).toInt
						if marks.isEmpty(i, j) && isTooBright(i, j) && pars.withinSmRange(i - x, j - y)
					) yield marks.makeBright(i, j)
				}
			}
		}
	}
	
	def calcSignals(pixels: Seq[(Int, Int)], pars: PPars, filter: Boolean): Array[MolStat] = {
		for(
			(x, y) <- pixels.toArray;
			val stats = {
						//print("calculating for (" + x + ", " + y + ")...");
						val stt = calcLocalStat(x, y, pars);
						//println(stt);
						stt}
			if(!filter || !stats.isWithin(data(y * XDim + x)))
		) yield {
			var molSum = 0d
			var num = 0
			for(
				i <- (x - pars.ImRad).toInt to (x + pars.ImRad).toInt;
				j <- (y - pars.ImRad).toInt to (y + pars.ImRad).toInt;
				if(pars.withinImRange(x - i, y - j))
			) {molSum += data(j * XDim + i); num +=1}
			val molStat = new MolStat
			molStat.x = x; molStat.y = y
			molStat.I = molSum - stats.mean * num
			molStat.background = stats.mean * num
			molStat
		}
	}
	
	def followMolecules(mols: Array[MolStat], pars: PPars): Array[MolStat] = {
		val maxs = detectLocalMaxs(pars)
		detectMolecules(maxs, pars)
		markBrightNonMolecules(maxs, pars)
		val newCoords = mols.map { mol =>
			val coord = (mol.x, mol.y)
			val newCoord = shiftToLocalMax(coord)
			if(pars.withinImRange(coord, newCoord) && isMolecule(newCoord, pars)) newCoord else coord 
		}
		calcSignals(newCoords, pars, false)
	}
	
	def addDataToArray(sumAccumulator: Array[Double]){
		assert(sumAccumulator.size == XDim * YDim, 
		    "Tried to accumulate Frame sum into an incompatible-size array.")
		for(i <- 0 to sumAccumulator.size - 1){
			sumAccumulator(i) += data(i)
		}
	}
	
	def calcSumInRoi(roi: ROI): Double = {
		var sum = 0d
		for(i <- roi.left to roi.right; j <- roi.top to roi.bottom){
			sum += data(j * XDim + i)
		}
		sum
	}
	
	def getImage : BufferedImage = {
		val image = new BufferedImage(XDim, YDim, BufferedImage.TYPE_3BYTE_BGR)
		val array = new Array[Int](XDim * YDim)
		var i = 0
		while(i < array.length){
			val ints = ((255 * num.toDouble(data(i) - min)) / num.toDouble(max - min)).toInt
			array(i) = ints + (ints << 8) + (ints << 16)
			i = i + 1
		}
		image.setRGB(0, 0, XDim, YDim, array, 0, XDim)
		image
	}
}
