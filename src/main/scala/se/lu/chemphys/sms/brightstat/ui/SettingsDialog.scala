package se.lu.chemphys.sms.brightstat.ui

import scala.swing._
import se.lu.chemphys.sms.brightstat.ROI

class SettingsDialog(owner: Window) extends Dialog(owner) {

	modal = true
	title = "BrightStat settings"
	
	
	private val highlightCheckBox = new CheckBox
	highlightCheckBox.selected = Main.movieWidget.movieScreen.showDetectedMols
	
	  
	private val inputsPanel = new GridPanel(1, 2){
		contents += new Label("Highlight detected molecules: ")
		contents += highlightCheckBox
	}
	
	private val cancelAction = Action("Cancel"){close()}
	
	private val okAction = Action("Ok"){
		import Main.movieWidget._
		initMolsToShow()
		movieScreen.showDetectedMols = highlightCheckBox.selected
		movieScreen.repaint()
		close()
	}
	
	private val buttonsPanel = new FlowPanel(FlowPanel.Alignment.Right)(
		new Button(okAction),
		new Button(cancelAction)
	)
	
	contents = new BoxPanel(Orientation.Vertical){
		border = Swing.EmptyBorder(5)
		contents += inputsPanel
		contents += buttonsPanel
	}
}




