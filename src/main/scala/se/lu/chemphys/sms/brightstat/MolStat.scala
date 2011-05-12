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
	private var nMols = 0
	
	def addMolStats(molStats: Array[MolStat], frame: Int){
		if(nMols == 0) nMols = molStats.size
		if(nMols != molStats.size) throw new IllegalArgumentException("The number of molecules must" +
			" be constant (changed from " + nMols + " to " + molStats.size + " on frame " + frame + ")")
		molStatsSilo += ((frame, molStats))
	}
	
	def getNMols = nMols
	
	def printIntensityReport(out: PrintStream){
		out.println("Frame\tMol" + (1 to getNMols).mkString("Mol\t"))
		for((k, v) <- molStatsSilo){
			out.print(k.toString + "\t")
			out println v.map(_.I).mkString("\t")
		}
	}
}