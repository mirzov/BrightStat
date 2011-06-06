package se.lu.chemphys.sms.brightstat.ui

import scala.swing._
import scala.swing.Swing._
import scala.swing.event.ValueChanged

class ControlWidget(movieWidget: MovieWidget) extends StatefulUiComponent{

	val startAction = Action("Start!") {Main.state ! "start"}
	val cancelAction = Action("Cancel!"){Main.state ! "cancel"}
	val button = new Button{
		text = "Start!"
		preferredSize = (90, 25)
		enabled = false
	}
	
	val dimensionsLabel = new Label("0 x 0 x 0")
	def defCursorText = "Cursor at: ? x ? x " + movieWidget.currentFrame
	val cursorLabel = new Label(defCursorText)
	cursorLabel.listenTo(movieWidget.movieScreen.mouse.moves)
	cursorLabel.listenTo(movieWidget.movieSlider)
	cursorLabel.reactions += {
		case mm: event.MouseMoved =>
		  	val coords = movieWidget.movieScreen.lookup(mm.point)
			cursorLabel.text = "Cursor at: %d x %d x %d".format(coords._1, coords._2, movieWidget.currentFrame)
		case mex: event.MouseExited => cursorLabel.text = defCursorText
		case valChange: ValueChanged if valChange.source == movieWidget.movieSlider => cursorLabel.text = defCursorText
	}
	val movieParamsPanel = new BoxPanel(Orientation.Vertical){
		border = CompoundBorder(BeveledBorder(Lowered), EmptyBorder(3))
		contents ++= List(new Label("Movie dimensions"), new Label("(XDim x YDim x NFrames):"), dimensionsLabel, cursorLabel)
		xLayoutAlignment = 1
	}
	
	private def ltrbToRoiLegend(l: Int, t: Int, r: Int, b: Int) = "(%d, %d)-(%d, %d)".format(l, t, r, b)
	val roiLabel = new Label("(?, ?)-(?, ?)")
	def setRoi(){
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
		contents ++= List(new Label("ROI dimensions,"), new Label("(left, top)-(right, bottom):"), roiLabel)
		xLayoutAlignment = 1
	}
	
	val setExRoiButton = new Button(){
		tooltip = "Set Ex signals ROI"
		preferredSize = (55, 25)
		action = Action("Ex"){
			Main.state ! "setexroi"
		}
	}

	val setEmRoiButton = new Button(){
		tooltip = "Set Em signals ROI"
		preferredSize = (55, 25)
		action = Action("Em"){
			Main.state ! "setemroi"
		}
	}
	
	val emexRoiButtons = new FlowPanel(){
		contents ++= setExRoiButton :: setEmRoiButton :: Nil
		maximumSize = (150, 40)
		xLayoutAlignment = 1
	}
	val mainButtonPanel = new BoxPanel(Orientation.NoOrientation){
		contents ++= Swing.HGlue :: button :: Nil
	}
	val topControlsPanel = new BoxPanel(Orientation.Vertical){
		contents ++= movieParamsPanel :: roiPanel :: emexRoiButtons :: Nil//Swing.VGlue :: mainButtonPanel :: Nil
	}
	
	val controlPanel = new BorderPanel(){
		border = EmptyBorder(3)
		import BorderPanel.Position._
		add(topControlsPanel, North)
		add(mainButtonPanel, South)
	}
	
	def toReady(){
		dimensionsLabel.text = Main.movie.XDim + " x " + Main.movie.YDim + " x " + Main.movie.Nframes
		button.action = startAction
		//button.enabled = true
		setRoi()
	}
	def toProcessing(){
		button.action = cancelAction
	}

}