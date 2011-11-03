package se.lu.chemphys.sms.brightstat

import scala.collection.immutable.TreeMap
import java.io.File
import se.lu.chemphys.sms.util.TsvDataTable

class DefaultBrightStat extends BrightStat{
	import BrightStatSaver._
  
	protected var molStatsSilo = TreeMap[Int, Array[MolStat]]()
	protected var exSignalsSilo = TreeMap[Int, Double]()
	protected var emSignalsSilo = TreeMap[Int, Double]()
	protected var nMols = 0

	def this(speFile: File){
		this()
		val coorKinFile = coordKinFile(speFile)
		if(coorKinFile.exists()){
			val coorKinTable = new TsvDataTable(coorKinFile)
			//println(coorKinTable.columnNames.mkString(" "))
			nMols = (coorKinTable.columnNames.length - 1) / 2
			coorKinTable.rows.foreach{ row =>
				val frame = row("Frame").toInt
				val molStats = (1 to nMols).toArray.map{
					molN => new MolStat{
									x = row(2 * molN - 1).toInt
									y = row(2 * molN).toInt
								}
				}
				molStatsSilo += ((frame, molStats))
			}
		}
	}
}

