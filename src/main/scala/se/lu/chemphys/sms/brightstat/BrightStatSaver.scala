package se.lu.chemphys.sms.brightstat

import java.io.File
import java.io.PrintStream

class BrightStatSaver(brightStat: BrightStat, moviePath: File) {

	def save(){
		val folder = moviePath.getParent
		val filename = moviePath.getName.toLowerCase.stripSuffix(".spe")
		val resFolder = new File(folder, filename)
		resFolder.mkdir
		
		def printReport(suffix: String, reportSelector: BrightStat => PrintStream => Unit){
			val file = new File(resFolder, filename + "_" + suffix + ".txt")
			val stream = new java.io.FileOutputStream(file)
			reportSelector(brightStat)(new PrintStream(stream))
			stream.close
		}
		
		printReport("coor", _.printCoordinatesReport)
		printReport("kin", _.printIntensityReport)
		printReport("bkgr", _.printBackgroundReport)
	}
}