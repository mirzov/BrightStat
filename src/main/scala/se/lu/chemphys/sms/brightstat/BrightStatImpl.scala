package se.lu.chemphys.sms.brightstat

import scala.collection.immutable.TreeMap
import java.io.File
import se.lu.chemphys.sms.util.TsvDataTable

class DefaultBrightStat extends BrightStat{
	import BrightStatSaver._
  
	def this(speFile: File){
		this()
		val coorKinFile = coordKinFile(speFile)
		if(coorKinFile.exists()){
			val coorKinTable = new TsvDataTable(coorKinFile)
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
		addInfoFromReport(kineticsFile(speFile), (ms, d) => ms.I = d)
		addInfoFromReport(backgroundFile(speFile), (ms, d) => ms.background = d)
	}
	
	private def addInfoFromReport(repFile: File, callback: (MolStat, Double) => Unit){
		if(repFile.exists()){
		  val repTable = new TsvDataTable(repFile)
		  repTable.rows.foreach{ row =>
			val frame = row("Frame").toInt
			val curnMols = repTable.columnNames.length - 1
			(1 to curnMols).toArray.foreach{ molN =>
			  callback(molStatsSilo(frame)(molN - 1), row("Mol" + molN).toDouble)
			}
		  }
		}
	}
}

