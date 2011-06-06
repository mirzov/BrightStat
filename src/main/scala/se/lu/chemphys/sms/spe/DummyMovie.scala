package se.lu.chemphys.sms.spe
import scala.collection.mutable.ArrayBuffer

class DummyMovie extends MovieFromFrames(DummyMovie.getFrames) 

object DummyMovie{
	def getFrames : Array[Frame[_]] = {
	  	val buff = new ArrayBuffer[Frame[_]]()
		for(i <- 1 to 20) buff += new Frame[Int](100, 100, new Array[Int](10000))
		buff.toArray
	}
}