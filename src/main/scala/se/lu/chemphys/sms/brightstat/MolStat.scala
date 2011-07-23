package se.lu.chemphys.sms.brightstat

import java.io.PrintStream
import scala.collection.immutable.TreeMap
import scala.collection.mutable.ArrayBuffer
class MolStat {
	var x = 0
	var y = 0
	var I = 0d
	var background = 0d
}

class BrightStat {

	private var molStatsSilo = TreeMap[Int, Array[MolStat]]()
	private var exSignalsSilo = TreeMap[Int, Double]()
	private var emSignalsSilo = TreeMap[Int, Double]()
	private var nMols = 0
	
	def nMolecules = nMols
	def getKinTrace(mol: Int): Seq[(Int, MolStat)] = {
		assert(mol >= 0 && mol < nMolecules, "The molecule number is out of range: " + mol)
		for((frame, molStats) <- molStatsSilo.toSeq) yield (frame, molStats(mol))
	}
	def getCoords(mol: Int, frame: Int): Option[(Int, Int)] = {
		molStatsSilo.get(frame).map(arr => (arr(mol).x, arr(mol).y))
//		val molStat = molStatsSilo(frame)(mol)
//		(molStat.x, molStat.y)
	}
	
	def addMolStats(molStats: Array[MolStat], frame: Int){
		if(nMols == 0) nMols = molStats.size
		if(nMols != molStats.size) throw new IllegalArgumentException("The number of molecules must" +
			" be constant (changed from " + nMols + " to " + molStats.size + " on frame " + frame + ")")
		molStatsSilo += ((frame, molStats))
	}
	
	def addExSignal(signal: Double, frame: Int){
		exSignalsSilo += ((frame, signal))
	}
	
	def addEmSignal(signal: Double, frame: Int){
		emSignalsSilo += ((frame, signal))
	}
	
	def getNMols = nMols
	
	private def printAnyIntensityReport(out: PrintStream, toIntens: MolStat => Double){
		out.println("Frame\tMol" + (1 to getNMols).mkString("\tMol"))
		for((k, v) <- molStatsSilo){
			out.print(k.toString + "\t")
			out println v.map(molstat => toIntens(molstat).formatted("%.2f")).mkString("\t")
		}
	}
	def printIntensityReport(out: PrintStream) = printAnyIntensityReport(out, _.I)
	def printBackgroundReport(out: PrintStream) = printAnyIntensityReport(out, _.background)
	
	def printCoordinatesKineticsReport(out: PrintStream){
		out.println("Frame" + (1 to getNMols).map(mn => "\tX" + mn + "\tY" + mn).mkString)
		for((k, v) <- molStatsSilo){
			out.print(k.toString)
			out println v.map(stat => "\t%d\t%d".format(stat.x, stat.y)).mkString
		}
	}
	
	def printCoordinatesReport(frame: Int, out: PrintStream){
		out.println("Molecule\tX\tY")
		val molStats = molStatsSilo(frame)
		for(i <- 0 to molStats.size - 1){
			out.println((i+1) + "\t" + (molStats(i).x + 1) + "\t" + (molStats(i).y + 1))
		}
	}
	
	def printCoordinatesReport(out: PrintStream){
		val minFrame = molStatsSilo.head._1
		printCoordinatesReport(minFrame, out)
	}
	
	private def printSignalsReport(out: PrintStream, silo:TreeMap[Int, Double]){
		import BrightStat._
		if(!silo.isEmpty) improveSignals(silo.values.toArray).foreach(out.println(_))
	}
	def printExSignalsReport(out: PrintStream) = printSignalsReport(out, exSignalsSilo)
	def printEmSignalsReport(out: PrintStream) = printSignalsReport(out, emSignalsSilo)
}

object BrightStat{
	import scala.math.{abs, sqrt, min}
	
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
	   var i = 0; var j = 0
	   while(i < N-1)
	   {
	      if(sigs(i+1) - sigs(i) > stepAver + 2.5 * stepStd)
	      {
	    	  j = i
	          while(sigs(j+1) > sigs(j) && j >= 0) {j -= 1}
	          starts += (if(j>=0) j + 1 else 0)
	          while(sigs(i+1) > sigs(i) && i < N-1){i += 1}
	      }
	      if(sigs(i) - sigs(i+1) > stepAver + 2.5 * stepStd)
	      {
	    	  j = i
	          while(sigs(j+1) < sigs(j) && j < N-1){j += 1}
	          if(j < N-1) {stops += j }
	          i=j
	      }
	      if(stops.length == starts.length - 1) stops += (N - 1)
	      i += 1
	   }
	   j = 0; var subtr = 0d; i = 0
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