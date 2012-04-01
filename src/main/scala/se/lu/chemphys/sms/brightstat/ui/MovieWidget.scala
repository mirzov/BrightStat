package se.lu.chemphys.sms.brightstat.ui

import java.awt.image.BufferedImage
import _root_.scala.swing.event.ValueChanged
import _root_.scala.swing._
import se.lu.chemphys.sms.spe.Movie

object MovieWidget{
  
	def point2ints(point: Point) = (point.getX.toInt, point.getY.toInt)

}

class MovieWidget(movie: => Movie, state: StateManager) extends StatefulUiComponent {
	import MovieWidget._
	
	private var _image = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
	def image = _image
	private def image_=(newImage: BufferedImage){_image = newImage}
	
	var showRoi = false
	
	val movieScreen = new MovieScreen(this, state)

	def initMolsToShow(){
		val coords = movie.brightStat.toSeq.flatMap{brStat =>
			(0 to brStat.nMolecules-1).flatMap{brStat.getCoords(_, frame)}
		}
		movieScreen.molsToShow = movieScreen.molsToShow.replaceCoordinates(coords)
	}
  
	val movieSlider = new Slider{
		orientation = Orientation.Horizontal
		min = 1
		paintLabels = true
		paintTicks = true
		paintTrack = true
	}
	
	private var frame = 1
	def currentFrame = frame
	def currentFrame_= (newFrame: Int){
		if(movie != null && newFrame >= 1 && newFrame <= movie.Nframes){
			movieSlider.value = newFrame
		}
	}
	
	val moviePanel = new BoxPanel(Orientation.Vertical){
		contents += movieScreen
		contents += movieSlider
		listenTo(movieSlider)
		reactions += {
		case valChange: ValueChanged if valChange.source == movieSlider => 
			frame = movieSlider.value
			image = movie.getFrame(frame).getImage
			initMolsToShow()
			movieScreen.repaint
		}
	}
	
	def initMovie(){
		currentFrame = 1
		movieSlider.max = movie.Nframes
		movieSlider.value = 1
		val step = (movie.Nframes / 10).max(1)
		movieSlider.majorTickSpacing = step
		movieSlider.labels = Map((1 to movie.Nframes by step).map{n => (n, new Label(n.toString))}:_*)
		//movieSlider.minorTickSpacing = (movie.Nframes / 10).max(1)
		
		println("Tick spacing = " + movieSlider.majorTickSpacing)
		println("Nframes = " + movie.Nframes)
	  	movieScreen.repaint()
	}

	def toReady(){
		movieSlider.enabled = true
		movieScreen.deafTo(movieScreen.mouse.moves)
	  	initMolsToShow()
	  	movieScreen.repaint()
	}
	
	def toProcessing(){
		movieSlider.enabled = false
	}
	
	override def toRoiSelection(){
		showRoi = true
		movieScreen.listenTo(movieScreen.mouse.moves)
	}
}