package se.lu.chemphys.sms.brightstat.tests

import se.lu.chemphys.sms.brightstat._
import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import java.io.File
import se.lu.chemphys.sms.util.TsvDataTable

class BrightStatTests extends JUnitSuite{
	import BrightStat._

	@Test def signalStepStatTest(){
		import SignalsImprover._
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
	
	private def testProblematicSignals(namePart: String){
		val url = this.getClass.getResource("/problematic_" + namePart + "_signals.txt")
		val file = new File(url.getFile)
		val tbl = new TsvDataTable(file)
		var signals = tbl.rows.map{r => r("Signals").toDouble}.toArray 
		val improved = improveSignals(signals)
		assert(improved.length === 1000)
		val numOfZeros = improved.filter(_ == 0).size
		assert(numOfZeros < 990)
		assert(numOfZeros > 900)
	}
	
	@Test def improveProblematicEmSignalsTest(){
		testProblematicSignals("em")
	}
	
	@Test def improveProblematicExSignalsTest(){
		testProblematicSignals("ex")
	}
	
//	@Test def problematicSignalStepStatTest(){
//		var (aver, dev) = getStepStat(getProblematicSignals)
//		println("Aver = " + aver)
//		println("Dev = " + dev)
//	}
	
}
