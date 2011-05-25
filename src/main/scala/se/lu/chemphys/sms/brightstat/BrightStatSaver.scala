package se.lu.chemphys.sms.brightstat

import java.io.File
import java.io.PrintStream

class BrightStatSaver(brightStat: BrightStat, moviePath: File) {

	def save(){
		val folder = moviePath.getParent
		val filename = moviePath.getName.toLowerCase.stripSuffix(".spe")
		val resFolder = new File(folder, filename)
		resFolder.mkdir
		
		def printReport(file: File, reportSelector: BrightStat => PrintStream => Unit){
			val stream = new java.io.FileOutputStream(file)
			reportSelector(brightStat)(new PrintStream(stream))
			stream.close
		}
		
		def suffixedFile(suffix: String) = new File(resFolder, filename + "_" + suffix + ".txt")

		printReport(suffixedFile("coor"), _.printCoordinatesReport)
		printReport(suffixedFile("kin"), _.printIntensityReport)
		printReport(suffixedFile("bkgr"), _.printBackgroundReport)
		
		printReport(new File(resFolder, "SignalsEx.txt"), _.printExSignalsReport)
		printReport(new File(resFolder, "SignalsEm.txt"), _.printEmSignalsReport)
	}
}