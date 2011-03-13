package se.lu.chemphys.sms.brightstat;

import scala.swing.SwingWorker

class PerformanceMeter extends SwingWorker{
	import ScalaTest.perfLabel
	import java.lang.System.{currentTimeMillis => curTime}
	
	private var startTime = curTime
	def act(){
		loop{
			react{
				case count : Int =>
					val duration = curTime - startTime
					val performance = count * 1000 / duration
					perfLabel.text = performance.toString + " fps"
					startTime = curTime
			}
		}
	}
}