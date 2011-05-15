package se.lu.chemphys.sms.brightstat.ui;

import se.lu.chemphys.sms.spe.MovieFromSpeFile
import se.lu.chemphys.sms.brightstat.MolStat
import scala.collection.mutable.ArrayBuffer
import se.lu.chemphys.sms.brightstat.PPars
import se.lu.chemphys.sms.spe.Movie
import java.awt.geom.AffineTransform
import swing._
import Swing._
import swing.event._
import java.awt.image.BufferedImage
import java.awt.Color
import java.io.File

object Main extends SimpleSwingApplication{

	val state = new StateManager
	var pars = new PPars()
	val movieWidget = new MovieWidget
	
	val button = new Button{
		preferredSize = (90, 25)
	}
	
	val startAction = Action("Start!") {state ! "start"}
	val cancelAction = Action("Cancel!"){state ! "cancel"}

	private var _movie: Movie = null
	def movie = _movie 
	def movie_= (newMovie: Movie){
		_movie = newMovie
		movieWidget.reset()
	}
	
	var movieFile: File = null
	
	val controlPanel = new FlowPanel(FlowPanel.Alignment.Left)(button){
		hGap = 30
	}

	val top = new MainFrame{
		title = "BrightStat"
		contents = new BorderPanel(){
			import BorderPanel.Position._
			add(controlPanel, South)
			add(movieWidget.moviePanel, Center)
		}
		menuBar = new BrightStatMenuBar()
		size = new Dimension(640, 480)
		centerOnScreen()
		state.start
	}

	private val url = this.getClass.getResource("/test.SPE")
	movieFile = new File(url.getFile)
	movie = new MovieFromSpeFile(movieFile.getAbsolutePath)
	
}




