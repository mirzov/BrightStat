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
	def defCursorText = "Cursor at: ? x ? x " + Main.movieWidget.currentFrame
	val cursorLabel = new Label(defCursorText)
	cursorLabel.listenTo(movieWidget.movieScreen.mouse.moves)
	cursorLabel.reactions += {
		case mm: event.MouseMoved =>
		  	val coords = movieWidget.movieScreen.lookup(mm.point)
			cursorLabel.text = format("Cursor at: %1$d x %2$d x %3$d", coords._1, coords._2, Main.movieWidget.currentFrame)
		case mex: event.MouseExited => cursorLabel.text = defCursorText
	}
	val movieParamsPanel = new BoxPanel(Orientation.Vertical){
		border = CompoundBorder(BeveledBorder(Lowered), EmptyBorder(3))
		contents += new Label("Movie dimensions")
		contents += new Label("(XDim x YDim x NFrames):")
		contents += dimensionsLabel
		contents += cursorLabel
	}
	
	private def ltrbToRoiLegend(l: Int, t: Int, r: Int, b: Int) = format("(%1$d, %2$d)-(%3$d, %4$d)", l, t, r, b)
	val roiLabel = new Label("(?, ?)-(?, ?)")
	def setRoi = {
		val roi = Main.pars.getSafeRoi(Main.movie.XDim, Main.movie.YDim)
		roiLabel.text = ltrbToRoiLegend(roi.left, roi.top, roi.right, roi.bottom)
	}
	def updateRoi(rect: java.awt.Rectangle){
		val (l, t) = (rect.getX.toInt, rect.getY.toInt)
		val (r, b) = (l + rect.getWidth.toInt, t + rect.getHeight.toInt)
		roiLabel.text = ltrbToRoiLegend(l, t, r, b)
	}
	val roiPanel = new BoxPanel(Orientation.Vertical){
		border = CompoundBorder(BeveledBorder(Lowered), EmptyBorder(3))
		contents += new Label("ROI dimensions,")
		contents += new Label("(left, top)-(right, bottom):")
		contents += roiLabel
	}
	
	val controlPanel = new BoxPanel(Orientation.Vertical){
		border = Swing.EmptyBorder(5)
		contents += movieParamsPanel
		contents += roiPanel
		contents += button
	}
	
	def toReady(){
		dimensionsLabel.text = Main.movie.XDim + " x " + Main.movie.YDim + " x " + Main.movie.Nframes
		button.action = startAction
		setRoi
	}
	def toProcessing(){
		button.action = cancelAction
	}

}