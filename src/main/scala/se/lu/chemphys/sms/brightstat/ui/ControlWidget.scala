package se.lu.chemphys.sms.brightstat.ui

import scala.swing._
import scala.swing.Swing._

class ControlWidget(movieWidget: MovieWidget) extends StatefulUiComponent{

	val startAction = Action("Start!") {Main.state ! "start"}
	val cancelAction = Action("Cancel!"){Main.state ! "cancel"}
	val button = new Button{
		preferredSize = (90, 25)
	}
	
	val dimensionsLabel = new Label("0 x 0 x 0")
	val cursorLabel = new Label("Cursor at: ?, ?")
	cursorLabel.listenTo(movieWidget.movieScreen.mouse.moves)
	cursorLabel.reactions += {
		case mm: event.MouseMoved =>
		  	val coords = movieWidget.movieScreen.lookup(mm.point)
			cursorLabel.text = "Cursor at: " + coords._1 + ", " + coords._2
		case mex: event.MouseExited => cursorLabel.text = "Cursor at: ?, ?"
	}
	val movieParamsPanel = new BoxPanel(Orientation.Vertical){
		border = CompoundBorder(BeveledBorder(Lowered), EmptyBorder(3))
		contents += new Label("Movie dimensions")
		contents += new Label("(XDim x YDim x NFrames):")
		contents += dimensionsLabel
		contents += cursorLabel
	}

	val controlPanel = new BorderPanel(){
		border = Swing.EmptyBorder(5)
		import BorderPanel.Position._
		add(movieParamsPanel, North)
		add(button, South)
	}
	
	def toReady(){
		dimensionsLabel.text = Main.movie.XDim + " x " + Main.movie.YDim + " x " + Main.movie.Nframes
		button.action = startAction
	}
	def toProcessing(){
		button.action = cancelAction
	}

}