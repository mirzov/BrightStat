package se.lu.chemphys.sms.brightstat.tests

import se.lu.chemphys.sms.spe.Utils
import se.lu.chemphys.sms.brightstat._
import se.lu.chemphys.sms.spe.Movie
import org.junit.Test
import org.junit.Assert._
import scala.io.Source

class MovieTests extends BrightStatSuite{

	private def getPars(nOfStartFrames: Int) = 
		new PPars{
			ImRad = 2.24f
			SmRad = 7f
			BrightNum = 8
			BrightSize = 2.24f
			NofStartFrames = nOfStartFrames
			CutOff = 0.3f
			NoiseSigms = 3f
			Correlation = 0.6f
			UseExProfile = false
			Normalize = false
			UseROI = false
		}

	private def molDetectionTest(nOfStartFrames: Int){
		val pars = getPars(nOfStartFrames)
		val url = this.getClass.getResource("/test_coor" + nOfStartFrames + "fr.txt")
		val src = Source.fromFile(url.toURI, "utf-8")
		val expectedCoords = src.getLines.drop(1).map(s => s.trim.split('\t'))
			.filter(_.size == 4).map(arr => (arr(1).toInt, arr(2).toInt)).toArray
		
		var detected = uint16.detectMoleculesFromScratch(pars)
		for(f <- (pars.NofStartFrames - 1) to 1 by -1){
			detected = uint16.getFrame(f).followMolecules(detected, pars)
		}
		val gotCoords = detected.map(stat => (stat.x + 1, stat.y + 1)).toArray
		gotCoords foreach println
		assertEquals(expectedCoords.size, gotCoords.size)
		val mismatches = gotCoords.zip(expectedCoords).filter(p => p._1 != p._2)
		mismatches foreach println
		assertEquals(0, mismatches.size)
	}

	@Test def detectMoleculesFromScratchTest(){
		//molDetectionTest(10)
		val pars = getPars(10)
		pars.roi = ROI(85, 153, 92, 159)
		pars.UseROI = true
		val (x, y) = (89, 157)
		val frame = uint16.getFrame(9)
		println("isCompact = " + frame.isCompact(x, y, pars))
		println("isSignificant = " + frame.isSignificant(x, y, pars))
		val detected = uint16.detectMoleculesFromScratch(pars)
		val gotCoords = detected.map(stat => (stat.x + 1, stat.y + 1)).toArray
		gotCoords foreach println
	}
	
}
