package se.lu.chemphys.sms.brightstat

import java.io.PrintStream
import scala.collection.immutable.TreeMap
import scala.collection.mutable.ArrayBuffer
import java.util.Locale

trait BrightStat {

	protected var molStatsSilo = TreeMap[Int, Array[MolStat]]()
	protected var exSignalsSilo = TreeMap[Int, Double]()
	protected var emSignalsSilo = TreeMap[Int, Double]()
	protected var nMols = 0
	
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
	
	private def doubleToString(d: Double) = String.format(Locale.US, "%.2f", d.asInstanceOf[java.lang.Double])
		
	private def printAnyIntensityReport(out: PrintStream, toIntens: MolStat => Double){
		out.println("Frame\tMol" + (1 to nMolecules).mkString("\tMol"))
		for((k, v) <- molStatsSilo){
			out.print(k.toString + "\t")
			out println v.map(molstat => doubleToString(toIntens(molstat))).mkString("\t")
		}
	}
	def printIntensityReport(out: PrintStream) = printAnyIntensityReport(out, _.I)
	def printBackgroundReport(out: PrintStream) = printAnyIntensityReport(out, _.background)
	
	def printCoordinatesKineticsReport(out: PrintStream){
		out.println("Frame" + (1 to nMolecules).map(mn => "\tX" + mn + "\tY" + mn).mkString)
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
	
	private def printSignalsReport(out: PrintStream, silo:TreeMap[Int, Double], improve: Boolean){
		import BrightStat._
		if(!silo.isEmpty){
			val sigs = if(improve) improveSignals(silo.values.toArray) else silo.values.toArray
			sigs.foreach(d => out.println(doubleToString(d)))
		}
	}
	def printExSignalsReport(out: PrintStream, improve: Boolean) = printSignalsReport(out, exSignalsSilo, improve)
	def printEmSignalsReport(out: PrintStream, improve: Boolean) = printSignalsReport(out, emSignalsSilo, improve)
	
	def removeMolecules(nums: Seq[Int]){
	  molStatsSilo = molStatsSilo.map{ pair =>
	    val (frame, mols) = pair
	    val newMols = (0 to nMols - 1).toArray.diff(nums).map(mols(_))
	    (frame, newMols)
	  }
	  for((frame, mols) <- molStatsSilo.headOption) nMols = mols.length
	}
}

object BrightStat{
	
	def improveSignals(sigs: Array[Double]): Array[Double] = SignalsImprover.improveSignalsSimple(sigs)
	
}