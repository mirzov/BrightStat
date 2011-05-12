package se.lu.chemphys.sms.brightstat.tests

import se.lu.chemphys.sms.brightstat._
import se.lu.chemphys.sms.spe.Movie

import org.junit.Test
import org.junit.Assert._

class MovieTests extends BrightStatSuite{

	@Test def detectMoleculesFromScratchTest(){
		val pars = new PPars{
			roi = ROI(uint16.XDim / 4, uint16.YDim / 4, uint16.XDim - uint16.XDim / 4, uint16.YDim - uint16.YDim / 4)
			UseROI = true
		}
		val detected = uint16.detectMoleculesFromScratch(1, pars)
		detected foreach println
		assertTrue(detected.length > 0)
	}
	
}