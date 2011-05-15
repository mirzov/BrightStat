package se.lu.chemphys.sms.brightstat.ui
import scala.swing._
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform
import scala.swing.event.ValueChanged

class MovieWidget {

	private var image = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)

	private val movieScreen = new BorderPanel{
		border = Swing.BeveledBorder(Swing.Raised)
		def transform : AffineTransform = {
			val insets = border.getBorderInsets(this.peer)
			val width = size.width - insets.right - insets.left
			val height = size.height - insets.bottom - insets.top
			val scaleX = width.toDouble / image.getWidth
			val scaleY = height.toDouble / image.getHeight
			new AffineTransform(scaleX, 0, 0, scaleY, insets.left, insets.top)
		}
		override def paint(g: Graphics2D){
			g.drawImage(image, transform, null)
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

	def reset(){
		currentFrame = 1
		movieSlider.max = Main.movie.Nframes
		movieSlider.value = 1
	}
}