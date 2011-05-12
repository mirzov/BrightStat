package se.lu.chemphys.sms.brightstat.ui;

import se.lu.chemphys.sms.brightstat.PPars
import se.lu.chemphys.sms.spe.Movie
import java.awt.geom.AffineTransform
import swing._
import Swing._
import swing.event._
import java.awt.image.BufferedImage
import java.awt.Color

object Main extends SimpleSwingApplication{

	val state = new StateManager
	
	val button = new Button{
		preferredSize = (90, 25)
	}
	
	var pars = new PPars()
	
	val startAction = Action("Start!") {state ! "start"}
	val pauseAction = Action("Pause!"){state ! "pause"}

	val resetButton = new Button{
		action = Action("Reset!") {state ! "reset"}
	}
		
	var image = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
	private val url = this.getClass.getResource("/test.SPE")
	var movie = new Movie(url.getFile)
	
	private var frame = 1
	def currentFrame = frame
	def currentFrame_= (newFrame: Int){
		if(movie != null && newFrame >= 1 && newFrame <= movie.Nframes){
			image = movie.getFrame(newFrame).getImage
			frame = newFrame
		}
	}
	
	val movieScreen = new BorderPanel{
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
	
	val perfLabel = new Label("0")
	
	val controlPanel = new FlowPanel(FlowPanel.Alignment.Left)(
			button, resetButton, perfLabel
		){hGap = 30}
	
	val top = new MainFrame{
		title = "BrightStat"
		contents = new BorderPanel(){
			import BorderPanel.Position._
			add(controlPanel, South)
			add(movieScreen, Center)
		}
		menuBar = new BrightStatMenuBar()
		size = new Dimension(640, 480)
		centerOnScreen()
		state.start
	}

}




