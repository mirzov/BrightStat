package se.lu.chemphys.sms.brightstat.tests

import org.scalatest.junit.JUnitSuite
import java.io.File
import se.lu.chemphys.sms.spe.Movie

import org.junit.Before
import org.junit.Test
import org.junit.Assert._

class MovieTests extends JUnitSuite{

	var movie: Movie = _
	
	@Before def initialize() {
		var url = this.getClass.getResource("/test.SPE")
		val file = new File(url.getFile)
		movie = new Movie(file.getAbsolutePath)
	}
	
	@Test def parametersTest(){
		assertEquals(50, movie.Nframes)
		assertEquals(272, movie.XDim)
		assertEquals(323, movie.YDim)
	}
	
	@Test def frameTests1(){
		val frame = movie.getFrame(1)
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
		val frame = movie.getFrame(24)
		assertEquals(17679, frame.max)
		assertEquals(3387, frame.min)
		assertEquals(15930, frame(131, 46))
		assertEquals(3608, frame(266, 317))
	}
}