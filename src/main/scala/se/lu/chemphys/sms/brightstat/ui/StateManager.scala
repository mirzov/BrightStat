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
					case _ => 
				}
			}
		}
		
		def processing() {
			toProcessing()
			val calculator = new BrightStatCalculator(this, pars, i => movieWidget.currentFrame = i)
		  	calculator.start()
			loop{
				react{
					case "cancel" => calculator.cancelled = true; ready()
					case "quit" => calculator.cancelled = true; quit()
					case result: BrightStat => new BrightStatSaver(result, movieFile).save(); ready()
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
						//control widget's roi info update here
					case _ => println("Stuck in roiselection")
				}
			}
		}
		
		initial
	}
}