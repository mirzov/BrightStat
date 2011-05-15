package se.lu.chemphys.sms.brightstat

import java.io.File
import java.io.PrintStream

class BrightStatSaver(brightStat: BrightStat, moviePath: File) {

	def save(){
		val folder = moviePath.getParent
		val filename = moviePath.getName.toLowerCase.stripSuffix(".spe")
		val resFolder = new File(folder, filename)
		resFolder.mkdir
		
		val coorFile = new File(resFolder, filename + "_coor.txt")
		//println("Coordinates report file = " + coorFile.getAbsolutePath)
		val coorStream = new java.io.FileOutputStream(coorFile)
		brightStat.printCoordinatesReport(new PrintStream(coorStream))
		coorStream.close
		
		val kinFile = new File(resFolder, filename + "_kin.txt")
		val kinStream = new java.io.FileOutputStream(kinFile)
		brightStat.printIntensityReport(new PrintStream(kinStream))
		kinStream.close
	}

}