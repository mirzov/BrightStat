package se.lu.chemphys.sms.brightstat

import scala.collection.mutable.ArrayBuffer
import scala.math.{abs, sqrt, min}

object SignalsImprover{
	
	def improveSignalsSimple(sigs: Array[Double]): Array[Double] = {
		val max = sigs.max
		val mean = sigs.sum / sigs.length
		val threshold = mean + (max - mean) / 5
		sigs.map(s => if(s > threshold) s - threshold else 0)
	}
  
	def getStepStat(sigs: Array[Double]): (Double, Double) = {
		val n = sigs.length
		assert(n > 1, "There must be at least 2 signal values to calculate step statistics.")
		val steps = sigs.sliding(2).map(arr => abs(arr(0) - arr(1))).toArray
		val aver = steps.sum / (n - 1)
		val sqAver = steps.map(step => step * step).sum / (n - 1)
		val std = sqrt(sqAver - aver * aver)
		(aver, std)
	}
	
	def improveSignals(sigs: Array[Double]): Array[Double] = {
	   val (stepAver, stepStd) = getStepStat(sigs)
	   var N = sigs.length
	   var starts = new ArrayBuffer[Int](); var stops = new ArrayBuffer[Int]()
	   var i = 0; var j = 0; var on = false
	   while(i < N-1)
	   {
	      if( (sigs(i+1) - sigs(i) > stepAver + 2.5 * stepStd) && !on)
	      {
	    	  j = i
	          while(sigs(j+1) > sigs(j) && j > 0) {j -= 1}
	          starts += (if(j>=0) j + 1 else 0)
	          on = true
	          while(sigs(i+1) > sigs(i) && i < N-1){i += 1}
	      }
	      if( (sigs(i) - sigs(i+1) > stepAver + 2.5 * stepStd) && on)
	      {
	    	  j = i
	          while(sigs(j+1) < sigs(j) && j < N-1){j += 1}
	          if(j < N-1) {stops += j }
	          on = false
	          i=j
	      }
	      i += 1
	   }
	   if(stops.length == starts.length - 1) stops += (N - 1)
	   j = 0; var subtr = 0d; i = 0
	   //println("Starts = " + starts.mkString(", "))
	   //println("Stops = " + stops.mkString(", "))
	   val improved = sigs.clone
	   while(i < starts.length)
	   {
	       while(j < starts(i)){improved(j)=0; j += 1};
	       subtr = min(improved(starts(i)), improved(stops(i)))
	       while(j <= stops(i)){improved(j) -= subtr; j += 1}
	       i += 1
	   }
	   while(j < N) {improved(j) = 0; j += 1}
	   improved
	}
}