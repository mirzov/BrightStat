package se.lu.chemphys.sms.brightstat

import java.io.File
import java.io.PrintStream

class BrightStatSaver(brightStat: BrightStat, moviePath: Option[File]) {
	import BrightStatSaver._
	
	private def printReport(file: File, reportSelector: BrightStat => PrintStream => Unit){
		val stream = new java.io.FileOutputStream(file)
		reportSelector(brightStat)(new PrintStream(stream))
		stream.close
	}
	
	def save(improveSignals: Boolean){
		for(mPath <- moviePath){
			val resFolder = getOutputFolder(mPath)
			resFolder.mkdir
			printMoleculeReports(mPath)
			printReport(new File(resFolder, "SignalsEx.txt"), bs => bs.printExSignalsReport(_, improveSignals))
			printReport(new File(resFolder, "SignalsEm.txt"), bs => bs.printEmSignalsReport(_, improveSignals))
		}
	}
	
	def rewriteMoleculeReports(){
	  for(mPath <- moviePath) printMoleculeReports(mPath)
	}
	
	private def printMoleculeReports(mPath: File){
		printReport(coordinatesFile(mPath), _.printCoordinatesReport)
		printReport(kineticsFile(mPath), _.printIntensityReport)
		printReport(backgroundFile(mPath), _.printBackgroundReport)
		printReport(coordKinFile(mPath), _.printCoordinatesKineticsReport)
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