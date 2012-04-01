package se.lu.chemphys.sms.brightstat.tests

import se.lu.chemphys.sms.spe.Utils
import se.lu.chemphys.sms.brightstat._
import se.lu.chemphys.sms.spe.Movie
import org.junit.{Test,Ignore}
import org.junit.Assert._
import scala.io.Source
import se.lu.chemphys.sms.brightstat.ui.BrightStatCalculator
import scala.actors.Actor

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
		//gotCoords.foreach(c => println(c._1 + "\t" + c._2))
		val mismatches = expectedCoords diff gotCoords
		mismatches foreach println
		assertEquals(0, mismatches.size)
	}

	@Test def detectMoleculesFromScratchTest(){
		molDetectionTest(10)
//		val pars = getPars(10)
//		pars.roi = ROI(85, 153, 92, 159)
//		pars.UseROI = true
//		val (x, y) = (89, 157)
//		val frame = uint16.getFrame(9)
//		println("isCompact = " + frame.isCompact(x, y, pars))
//		println("isSignificant = " + frame.isSignificant(x, y, pars))
//		val detected = uint16.detectMoleculesFromScratch(pars)
//		val gotCoords = detected.map(stat => (stat.x + 1, stat.y + 1)).toArray
//		gotCoords.foreach(c => println(c._1 + "\t" + c._2))
	}
	
	@Test def moreInitFramesMoreMoleculesTest(){
		def detectedCoords(nInitFrames: Int): Seq[(Int, Int)] = {
			val pars = getPars(10)
			pars.NoiseSigms = 0f
			pars.roi = ROI(48, 193, 75, 233)
			pars.UseROI = true
			val detected = uint16.detectMoleculesFromScratch(pars)
			detected.map(stat => (stat.x, stat.y))
		}
		val anExpectedMol = (69,212)
		val detected10 = detectedCoords(10)
		val detected30 = detectedCoords(30)
		assertTrue(detected10.contains(anExpectedMol))
		assertTrue(detected30.contains(anExpectedMol))
		assertTrue(detected30.size >= detected10.size)
	}
	
	@Test def brightStatCalculationTest(){
		val pars = getPars(10)
		//pars.roi = ROI(44, 155, 68, 172)	// small roi
		pars.roi = ROI(34, 149, 80, 179) 	// medium roi
		//pars.roi = ROI(10, 113, 112, 210) // large roi
		pars.UseROI = true
		val thisActor = Actor.self
		val calc = new BrightStatCalculator(uint16, pars, i => (), bs => thisActor ! bs)
		calc.start()
		
		val url = this.getClass.getResource("/uint16kinX56Y164.txt")
		val src = Source.fromFile(url.toURI, "utf-8")
		val expectedKin = src.getLines.drop(1).map(s => s.trim.split('\t'))
			.filter(_.size == 2).map(arr => arr(1).toDouble).toArray
		
		Actor.receive{
		  	case brightstat: BrightStat => {
		  		//println("Molecules detected: " + brightstat.nMolecules)
		  		//assertEquals(1, brightstat.nMolecules)
		  		//println("Molecule #0 has coordinates: " + brightstat.getCoords(0, 1))
		  		val trace = brightstat.getKinTrace(0).toArray
		  		assertEquals(expectedKin.length, trace.length)
		  		val discrs = for(i <- 0 to trace.length - 1) yield scala.math.abs(trace(i)._2.I - expectedKin(i))
		  		//println("Max difference: " + discrs.max)
		  		assertTrue(discrs.max < 145)
		  		//trace.map(_._2.I).zip(expectedKin).foreach(stat => println(stat._2 + "\t" + stat._1))
		  	}
		}
	}
	
}
