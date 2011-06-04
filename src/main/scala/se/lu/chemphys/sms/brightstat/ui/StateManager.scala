package se.lu.chemphys.sms.brightstat.ui;

import se.lu.chemphys.sms.brightstat._
import scala.swing.SwingWorker

trait StatefulUiComponent{
	def toInitial(){}
	def toReady(): Unit
	def toProcessing(): Unit
	def toRoiSelection(){}
}

class StateManager extends SwingWorker {
	import Main._

	var calculator: BrightStatCalculator = null
	
	def act(){
	  
	  	def initial() {
	  		toInitial()
			loop{
	  			react{
	  				case "quit" => quit()
	  				case "ready" => ready()
	  				case _ => 
	  			}
	  		}
	  	}
		
		def ready() {
			toReady()
			loop{
				react{
					case "start" =>	processing()
					case "quit" => quit()
					case "ready" => ready()
					case "selectroi" => roiselection()
					case "setexroi" => pars.exRoi = pars.roi
					case "setemroi" => pars.emRoi = pars.roi
					case _ => 
				}
			}
		}
		
		def processing() {
			toProcessing()
			val calculator = new BrightStatCalculator(movie, pars, i => movieWidget.currentFrame = i, bs => this ! bs)
		  	calculator.start()
			loop{
				react{
					case "cancel" => calculator.cancelled = true; ready()
					case "quit" => calculator.cancelled = true; quit()
					case result: BrightStat => 
					  	new BrightStatSaver(result, movieFile).save()
					  	movieWidget.currentFrame = pars.startFrame
					  	ready()
					case _ => 
				}
			}
		}
		
		def roiselection() {
			toRoiSelection()
			loop{
				react{
					case "finishselectingroi" => ready()
					case "noroi" => pars.UseROI = false
					case roi: java.awt.Rectangle =>
					  	pars.roi = movieWidget.getRoi
						Main.controlWidget.setRoi
					case _ =>
				}
			}
		}
		
		initial
	}
}