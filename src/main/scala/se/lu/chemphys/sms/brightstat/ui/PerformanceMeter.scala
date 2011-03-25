package se.lu.chemphys.sms.brightstat.ui;

import scala.swing.SwingWorker

class PerformanceMeter extends SwingWorker{
	import Main.perfLabel
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
				case "reset" => startTime = curTime
			}
		}
	}
}