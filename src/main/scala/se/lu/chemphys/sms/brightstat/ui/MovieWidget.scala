package se.lu.chemphys.sms.brightstat.ui

import scala.swing._
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform
import scala.swing.event.ValueChanged
import se.lu.chemphys.sms.brightstat.ROI
import se.lu.chemphys.sms.spe.Movie

object MovieWidget{
  
	def point2ints(point: Point) = (point.getX.toInt, point.getY.toInt)

}

class MovieWidget(movie: => Movie, state: StateManager) extends StatefulUiComponent {
	import MovieWidget._
	
	private var _image = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
	def image = _image
	private def image_=(newImage: BufferedImage){_image = newImage}
	
	val roirect = new java.awt.Rectangle()
	var showRoi = false
	
	def getRoi: ROI = {
		val (left, top) = point2ints(roirect.getLocation)
		val right = roirect.getWidth.toInt + left
		val bottom = roirect.getHeight.toInt + top
		ROI(left, top, right, bottom)
	}
	
	val movieScreen = new MovieScreen(this, state)
  
	val movieSlider = new Slider{
		orientation = Orientation.Horizontal
		min = 1
		paintLabels = true
		paintTicks = true
		paintTrack = true
		majorTickSpacing = 10
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
			movieScreen.repaint
		}
	}
	
	def initMovie(){
		currentFrame = 1
		movieSlider.max = movie.Nframes
		movieSlider.value = 1
	}

	def toReady(){
		movieSlider.enabled = true
		movieScreen.deafTo(movieScreen.mouse.moves)
	}
	
	def toProcessing(){
		movieSlider.enabled = false
	}
	
	override def toRoiSelection(){
		showRoi = true
		movieScreen.listenTo(movieScreen.mouse.moves)
	}
}