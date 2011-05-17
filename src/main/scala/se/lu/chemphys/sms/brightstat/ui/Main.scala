package se.lu.chemphys.sms.brightstat.ui;

import java.io.File
import scala.swing.Swing._
import scala.swing._
import se.lu.chemphys.sms.brightstat.PPars
import se.lu.chemphys.sms.spe.{Movie, MovieFromSpeFile}

object Main extends SimpleSwingApplication with StatefulUiComponent{

	val state = new StateManager
	val pars = new PPars()
	
	val movieWidget = new MovieWidget
	val controlWidget = new ControlWidget(movieWidget)
	val brightStatMenuBar = new BrightStatMenuBar()
	
	private val statefulUi: Seq[StatefulUiComponent] = Seq(
		movieWidget, controlWidget, brightStatMenuBar
	)
	
	def toReady() = statefulUi.foreach(_.toReady())
	def toProcessing() {
		statefulUi.foreach(_.toProcessing())
		pars.startFrame = movieWidget.currentFrame
	}
	override def toRoiSelection() = {
		statefulUi.foreach(_.toRoiSelection())
		pars.UseROI = true
	}
	
	var movieFile: File = null
	private var _movie: Movie = null
	def movie = _movie 
	def movie_= (newMovie: Movie){
		_movie = newMovie
		state ! "ready"
	}
	
	val top = new MainFrame{
		title = "BrightStat"
		contents = new BorderPanel(){
			import BorderPanel.Position._
			add(controlWidget.controlPanel, East)
			add(movieWidget.moviePanel, Center)
		}
		menuBar = brightStatMenuBar
		size = new Dimension(640, 480)
		centerOnScreen()
	}

	state.start
	private val url = this.getClass.getResource("/test.SPE")
	movieFile = new File(url.getFile)
	movie = new MovieFromSpeFile(movieFile.getAbsolutePath)
	
}




