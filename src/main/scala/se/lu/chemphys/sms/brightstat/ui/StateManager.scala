package se.lu.chemphys.sms.brightstat.ui;

import Main._
import se.lu.chemphys.sms.brightstat.BrightStat
import se.lu.chemphys.sms.brightstat.BrightStatSaver
import scala.swing.SwingWorker

class StateManager extends SwingWorker {

	var calculator: BrightStatCalculator = null
	
	def act(){
		
		def ready : Nothing = {
			button.action = startAction
			movieWidget.reset
			react{
				case "start" =>	processing
				case "quit" => quit()
			}
		}
		
		def processing : Nothing = {
		  	pars.startFrame = movieWidget.currentFrame
			val calculator = new BrightStatCalculator(this, pars, i => movieWidget.currentFrame = i)
		  	calculator.start()
			button.action = cancelAction
			react{
				case "cancel" => calculator.cancelled = true; ready
				case "quit" => calculator.cancelled = true; quit()
				case result: BrightStat => new BrightStatSaver(result, movieFile).save(); ready
			}
		}
		
		ready
	}
}