package se.lu.chemphys.sms.brightstat.tests

import java.io.File
import se.lu.chemphys.sms.spe.Movie
import org.scalatest.junit.JUnitSuite

trait BrightStatSuite extends JUnitSuite{

	private def getMovie(filename: String): Movie = {
		val url = this.getClass.getResource("/" + filename)
		val file = new File(url.getFile)
		new Movie(file.getAbsolutePath)
	}
	
	lazy val uint16 = getMovie("test.SPE")
	lazy val float = getMovie("99_10_SHORT_FLOAT.SPE")
	lazy val byte = getMovie("99_10_SHORT_BYTE.SPE")
	lazy val int16 = getMovie("99_10_SHORT_INT16.SPE")
	lazy val long = getMovie("exc_profile.SPE")

}