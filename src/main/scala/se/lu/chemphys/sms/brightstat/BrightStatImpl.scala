package se.lu.chemphys.sms.brightstat

import scala.collection.immutable.TreeMap

class DefaultBrightStat extends BrightStat{
  
	protected var molStatsSilo = TreeMap[Int, Array[MolStat]]()
	protected var exSignalsSilo = TreeMap[Int, Double]()
	protected var emSignalsSilo = TreeMap[Int, Double]()
	protected var nMols = 0

}
