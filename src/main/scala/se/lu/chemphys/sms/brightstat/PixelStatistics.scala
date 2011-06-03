package se.lu.chemphys.sms.brightstat

class PixelStatistics(values: Seq[Double], pars: PPars) {
	var sum, stDevSum =  0d
	var num = 0
	values.foreach{v =>
		sum += v
		stDevSum += v * v
		num += 1
	}
	val mean = if (num == 0) 0d else sum / num
	val stdev = if (num == 0) 0d else scala.math.sqrt(stDevSum / num - mean * mean)
	
	def isWithin(value: Double): Boolean = value < scala.math.floor(mean + pars.NoiseSigms * stdev)
	def isOutside(value: Double): Boolean = value > scala.math.floor(mean + pars.NoiseSigms * stdev)

	override def toString = "(Mean = " + mean + ", StdDev = " + stdev + ")"
}
