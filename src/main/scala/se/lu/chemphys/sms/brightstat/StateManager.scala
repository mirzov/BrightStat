package se.lu.chemphys.sms.brightstat;

import scala.swing._
import ScalaTest._

class StateManager extends SwingWorker {
	var movieShower: MovieShower = null
	
	def act(){
		
		def initial : Nothing = {
			movieShower = new MovieShower
			button.action = startAction
			resetButton.enabled = false
			perfLabel.text = "0 fps"
			react{
				case "start" =>	movieShower.start(); showing
				case _ => initial
			}
		}
		
		def showing : Nothing = {
			button.action = pauseAction
			resetButton.enabled = false
			react{
				case "pause" => paused
				case _ => showing
			}
		}
		
		def paused : Nothing = {
			movieShower.cancelled = true;
			movieShower = new MovieShower
			button.action = startAction
			resetButton.enabled = true
			perfLabel.text = "0 fps"
			react{
				case "start" =>	movieShower.start(); showing
				case "reset" => movieShower.clearImage(); initial
				case _ => paused
			}
		}
		
		initial
	}
}