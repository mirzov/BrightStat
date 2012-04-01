package se.lu.chemphys.sms.brightstat.ui

import scala.swing._
import se.lu.chemphys.sms.brightstat.ROI

class PParsDialog(owner: Window) extends Dialog(owner) {

	modal = true
	title = "BrightStat preferences"
	
	private val inputLabels = Seq(
		("ImRad", 			"Molecule radius"),
		("SmRad", 			"Smoothing radius"),
		("BrightNum", 		"Molecule test (number of pixels)"),
		("BrightSize", 		"Molecule test (size in pixels)"),
		("Correlation", 	"Molecule test (brightness-distance correlation)"),
		("NoiseSigms", 		"Noise limit in sigmas"),
		("NofStartFrames", 	"NofFrames for detection"),
		("CutOff", 			"Excitation cutoff")
	)
	private val checkBoxLabels = Set(
		("ImproveSignals", 	"Improve the excitation/emission signals"),
		("UseExProfile", 	"Use excitation profile"),
		("Normalize",		"Normalize by excitation")
//		("UseROI",			"Use ROI")
	)
	
	import Main.pars
	private val inputs = inputLabels.map{case (key, label) =>
		(	key,
			new Label(label + ": "){horizontalAlignment = Alignment.Right},
			new TextField(pars.getNumericValue(key)){columns = 10})
	}
	private val checks = checkBoxLabels.map{case (key, label) =>
		(	key,
			new Label(label + ": "){horizontalAlignment = Alignment.Right},
			new CheckBox{ selected = pars.getBooleanValue(key)})
	}
	
	private val inputsPanel = new GridPanel(inputLabels.size + checkBoxLabels.size, 2){
		inputs.foreach{case (key, label, textField) => contents += label; contents += textField}
		checks.foreach{case (key, label, checkBox) => contents += label; contents += checkBox}
	}
	
	private val cancelAction = Action("Cancel"){close()}
	
	private val okAction = Action("Ok"){
		inputs.foreach{case (key, label, textField) =>
			try{
				Main.pars.setNumericValue(key, textField.text)
			}catch{
				case e => Dialog.showMessage(inputsPanel, "Problem parsing value for " + label.text +
							"\nvalue left unchanged",
						"Error", Dialog.Message.Error)
			}
		}
		checks.foreach{case (key, _, checkBox) => Main.pars.setBooleanValue(key, checkBox.selected)}
		close()
	}
	
	private val buttonsPanel = new FlowPanel(FlowPanel.Alignment.Right)(
		new Button(okAction),
		new Button(cancelAction)
	)
	
	contents = new BoxPanel(Orientation.Vertical){
		contents += inputsPanel
		contents += buttonsPanel
	}
}




