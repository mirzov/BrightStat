package se.lu.chemphys.sms.brightstat.ui;

import java.io.File
import scala.swing.Swing._
import scala.swing._
import se.lu.chemphys.sms.brightstat.PPars
import se.lu.chemphys.sms.spe._
import se.lu.chemphys.sms.brightstat.BrightStat

object Main extends SimpleSwingApplication with StatefulUiComponent{

	val state = new StateManager
	val pars = new PPars()
	
	val movieWidget = new MovieWidget(movie, state)
	val controlWidget = new ControlWidget(movieWidget)
	val brightStatMenuBar = new BrightStatMenuBar()
	
	private val statefulUi: Seq[StatefulUiComponent] = Seq(
		movieWidget, controlWidget, brightStatMenuBar
	)
	
	def toReady() = statefulUi.foreach(_.toReady())
	def toProcessing() {
		statefulUi.foreach(_.toProcessing())
		pars.startFrame = movieWidget.currentFrame
		movie.brightStat = None
		movieWidget.initMolsToShow()
	}
	override def toRoiSelection() = {
		statefulUi.foreach(_.toRoiSelection())
		pars.UseROI = true
	}
	
	var movieFile: Option[File] = None
	private var _movie: Movie = new DummyMovie
	movieWidget.initMovie()
	def movie = _movie 
	def movie_= (newMovie: Movie){
		_movie = newMovie
		movieWidget.initMovie()
		state ! "ready"
	}
	
	val top = new MainFrame{
		title = "BrightStat"
		contents = new BorderPanel(){
			import BorderPanel.Position._
			add(movieWidget.moviePanel, Center)
			add(controlWidget.controlPanel, East)
		}
		menuBar = brightStatMenuBar
		size = new Dimension(640, 480)
		centerOnScreen()
	}

	state.start
	private val url = this.getClass.getResource("/test.SPE")
	movieFile = Some(new File(url.getFile))
	movie = new MovieFromSpeFile(movieFile.get.getAbsolutePath)
	//println(controlWidget.emexRoiButtons.size)
}




