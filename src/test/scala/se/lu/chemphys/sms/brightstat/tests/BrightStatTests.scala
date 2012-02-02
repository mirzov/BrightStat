package se.lu.chemphys.sms.brightstat.tests

import se.lu.chemphys.sms.brightstat._
import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

class BrightStatTests extends JUnitSuite{
	import BrightStat._

	@Test def signalStepStatTest(){
		assertEquals((1, 0), getStepStat(Array[Double](0, 1, 2, 3)))
		assertEquals((1.5, 0.5), getStepStat(Array[Double](0, 1, 3)))
		var (aver, dev) = getStepStat(Array[Double](0, 1, 3, -2, 5, 8))
		assertEquals(3.6, aver, 0.0001)
		assertEquals(2.154, dev, 0.0001)
	}
	
	@Test def improveSignalsTest(){
		val sigs = Array.fill(30)(-0.1d)
		sigs(10) = 10; sigs(11) = 10
		val improvedArray = improveSignals(sigs)
		assert(improvedArray.length === sigs.length)
		
		val improved = improvedArray.zipWithIndex.filter(_._1 > 0).map(_._2)
		assertArrayEquals(Array(10, 11), improved)
	}
	
}
