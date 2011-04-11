package se.lu.chemphys.sms.brightstat.tests

import org.scalatest.junit.JUnitSuite
import java.io.File
import se.lu.chemphys.sms.spe.Movie

import org.junit.Before
import org.junit.Test
import org.junit.Assert._

class MovieTests extends JUnitSuite{

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
	
	@Test def getBrightClusterTest(){
		val frame = int16.getFrame(3)
		val cluster = frame.getBrightCluster(157, 104, 6).map(p => (p._1 + 1, p._2 + 1))
		println(cluster)
		assertEquals(6, cluster.size)
		val expected = Set((158,105), (157,105), (159,104), (157,104), (159,105), (158,104))
		assertEquals(expected, cluster)
	}
	
	@Test def parametersTest(){
		assertEquals(3, byte.Nframes)
		assertEquals(2, float.Nframes)
		assertEquals(4, int16.Nframes)
		assertEquals(1, long.Nframes)
		assertEquals(50, uint16.Nframes)
		
		assertEquals(272, uint16.XDim)
		assertEquals(323, uint16.YDim)
	}
	
	@Test def frameTests1(){
		val frame = uint16.getFrame(1)
		assertEquals(3392, frame(0,0))
		assertEquals(3652, frame(177,149))
		assertEquals(4972, frame(184,178))
		assertEquals(3851, frame(33,285))
		assertEquals(3659, frame(180,310))
		assertEquals(3619, frame(0,322))
		assertEquals(3623, frame(1,170))
		assertEquals(3546, frame(2,170))
		assertEquals(3504, frame(0,171))
		assertEquals(3473, frame(0,170))
		assertEquals(3582, frame(128,315))
		assertEquals(3510, frame(250,250))
		assertEquals(3475, frame(271,0))
		assertEquals(3542, frame(270,322))
		assertEquals(3493, frame(271,322))
		assertEquals(8337, frame.max)
		assertEquals(3382, frame.min)
	}
	
	@Test def frameTests2(){
		val frame = uint16.getFrame(24)
		assertEquals(17679, frame.max)
		assertEquals(3387, frame.min)
		assertEquals(15930, frame(131, 46))
		assertEquals(3608, frame(266, 317))
	}

	@Test def frameTests3(){
		var frame = float.getFrame(1)
		def f(i:Int, j:Int): Float = frame(i, j).asInstanceOf[Float]
		
		assertEquals(1222.04f, f(111, 114), 0.005f)
		assertEquals(1338.90f, f(220, 2), 0.005f)
		
		frame = float.getFrame(2)
		assertEquals(5128.55f, f(86, 102), 0.005f)
		assertEquals(1657.76f, f(227, 232), 0.005f)
	}

	@Test def frameTests4(){
		val frame = byte.getFrame(2)
		assertEquals(39.toShort, frame(94, 119))
		assertEquals(205.toShort, frame(105, 108))
	}

	@Test def frameTests5(){
		val frame = int16.getFrame(3)
		assertEquals((-217).toShort, frame(0, 0))
		assertEquals((-41).toShort, frame(199, 192))
		assertEquals(225.toShort, frame(154, 104))
	}

	@Test def frameTests6(){
		val frame = long.getFrame(1)
		assertEquals(279538, frame(342, 149))
		assertEquals(106739, frame(511, 511))
	}

	@Test def isLocalMaxTest(){
		var frame = float.getFrame(1)
		assertTrue(frame.isLocalMax(156, 105))
		frame = int16.getFrame(3)
		assertTrue(frame.isLocalMax(169, 69))
	}
	
}