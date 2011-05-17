package se.lu.chemphys.sms.brightstat.ui

import scala.swing._
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform
import scala.swing.event.ValueChanged
import se.lu.chemphys.sms.brightstat.ROI

class MovieWidget extends StatefulUiComponent {

	private var image = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
	private val roirect = new java.awt.Rectangle()
	private var showRoi = false
	
	def point2ints(point: Point) = (point.getX.toInt, point.getY.toInt)

	def getRoi: ROI = {
		val (left, top) = point2ints(roirect.getLocation)
		val right = roirect.getWidth.toInt + left
		val bottom = roirect.getHeight.toInt + top
		ROI(left, top, right, bottom)
	}
	
	val movieScreen = new BorderPanel{
		def transform : AffineTransform = {
			val width = size.width
			val height = size.height
			val scaleX = width.toDouble / image.getWidth
			val scaleY = height.toDouble / image.getHeight
			new AffineTransform(scaleX, 0, 0, scaleY, 0, 0)
		}
		def lookup(pixel: Point) = {
			val res = transform.inverseTransform(pixel, new Point())
			(res.getX.toInt + 1, res.getY.toInt + 1)
		}
		override def paint(g: Graphics2D){
			g.drawImage(image, transform, null)
			if(showRoi) {
				g.setColor(java.awt.Color.YELLOW)
				g.draw(roirect)
			}
		}
		
		listenTo(mouse.clicks)
		reactions += {
			case down: event.MousePressed  =>
			  	val (x, y) = point2ints(down.point)
				roirect.setBounds(x, y, 0, 0)
				Main.state ! "selectroi"
				//println (down)
			case drag: event.MouseDragged =>
			  	val (x, y) = point2ints(drag.point)
			  	roirect.add(x, y)
			  	repaint
			  	//println(drag)
			case up: event.MouseReleased =>
			  	if(point2ints(up.point) == point2ints(roirect.getLocation)){
			  		Main.state ! "noroi"
			  		showRoi = false
			  		repaint
			  	} else {Main.state ! roirect}
			  	Main.state ! "finishselectingroi"
			  	//println(up)
		}
	}
  
	private val movieSlider = new Slider{
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
		if(Main.movie != null && newFrame >= 1 && newFrame <= Main.movie.Nframes){
			movieSlider.value = newFrame
		}
	}
	
	private def refreshFrame(){
		frame = movieSlider.value
		image = Main.movie.getFrame(frame).getImage
		movieScreen.repaint
	}
	
	val moviePanel = new BoxPanel(Orientation.Vertical){
		contents += movieScreen
		contents += movieSlider
		listenTo(movieSlider)
		reactions += {
		case valChange: ValueChanged if valChange.source == movieSlider => 
			refreshFrame()
		}
	}

	def toReady(){
		currentFrame = 1
		movieSlider.max = Main.movie.Nframes
		movieSlider.value = 1
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