package se.lu.chemphys.sms.brightstat

import java.io.PrintStream
import scala.collection.immutable.TreeMap
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
		exSignalsSilo += ((frame, signal))
	}
	
	def getNMols = nMols
	
	private def printAnyIntensityReport(out: PrintStream, toIntens: MolStat => Double){
		out.println("Frame\tMol" + (1 to getNMols).mkString("\tMol"))
		for((k, v) <- molStatsSilo){
			out.print(k.toString + "\t")
			out println v.map(molstat => toIntens(molstat).formatted("%.2f")).mkString("\t")
		}
	}
	
	def printIntensityReport(out: PrintStream){
		printAnyIntensityReport(out, _.I)
	}
	
	def printBackgroundReport(out: PrintStream){
		printAnyIntensityReport(out, _.background)
	}
	
	def printCoordinatesReport(frame: Int, out: PrintStream){
		out.println("Molecule\tX\tY")
		val molStats = molStatsSilo(frame)
		for(i <- 0 to molStats.size - 1){
			out.println((i+1).toString + "\t" + (molStats(i).x + 1) + "\t" + (molStats(i).y + 1))
		}
	}
	
	def printCoordinatesReport(out: PrintStream){
		val minFrame = molStatsSilo.head._1
		printCoordinatesReport(minFrame, out)
	}
}