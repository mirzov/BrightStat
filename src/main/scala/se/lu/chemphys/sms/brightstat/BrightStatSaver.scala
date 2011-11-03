package se.lu.chemphys.sms.brightstat

import java.io.File
import java.io.PrintStream

class BrightStatSaver(brightStat: BrightStat, moviePath: Option[File]) {
	import BrightStatSaver._
	
	def save(){
		moviePath.foreach{ mPath =>
			val resFolder = getOutputFolder(mPath)
			resFolder.mkdir
			
			def printReport(file: File, reportSelector: BrightStat => PrintStream => Unit){
				val stream = new java.io.FileOutputStream(file)
				reportSelector(brightStat)(new PrintStream(stream))
				stream.close
			}
			
			printReport(coordinatesFile(mPath), _.printCoordinatesReport)
			printReport(kineticsFile(mPath), _.printIntensityReport)
			printReport(backgroundFile(mPath), _.printBackgroundReport)
			printReport(coordKinFile(mPath), _.printCoordinatesKineticsReport)
			
			printReport(new File(resFolder, "SignalsEx.txt"), _.printExSignalsReport)
			printReport(new File(resFolder, "SignalsEm.txt"), _.printEmSignalsReport)
		}
	}
}

object BrightStatSaver{
	
	def coordinatesFile(speFile: File) = suffixedFile(speFile, "coor")
	def kineticsFile(speFile: File) = suffixedFile(speFile, "kin")
	def backgroundFile(speFile: File) = suffixedFile(speFile, "bkgr")
	def coordKinFile(speFile: File) = suffixedFile(speFile, "coor_kin")
  
	def getOutputFolder(speFile: File): File = {
		val folder = speFile.getParent
		new File(folder, getPlainFilename(speFile))
	}
	
	private def getPlainFilename(speFile: File) = speFile.getName.toLowerCase.stripSuffix(".spe")
	private def suffixedFile(speFile: File, suffix: String) = new File(
	    getOutputFolder(speFile),
	    getPlainFilename(speFile) + "_" + suffix + ".txt"
	)
}