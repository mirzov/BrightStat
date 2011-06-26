package se.lu.chemphys.sms.brightstat.ui
import scala.swing.BorderPanel
import scala.swing.event
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform
import java.awt.Graphics2D
import java.awt.geom.Point2D
import java.awt.geom.Point2D.{Double => Point}
import java.awt.geom.Ellipse2D.{Double => Ellipse}

class MovieScreen(widget: MovieWidget, state: StateManager) extends BorderPanel {
	import MovieWidget._
	import widget._

	private var _molsToShow: Seq[(Int, Int)] = Array[(Int, Int)]()
	def molsToShow = _molsToShow
	def molsToShow_=(mols: Seq[(Int, Int)]){_molsToShow = mols; showDetectedMols = true}
	var showDetectedMols = false
	
	def transform : AffineTransform = {
		val width = size.width
		val height = size.height
		val scaleX = width.toDouble / image.getWidth
		val scaleY = height.toDouble / image.getHeight
		new AffineTransform(scaleX, 0, 0, scaleY, 0, 0)
	}
	def lookup(pixel: Point2D) = {
		val res = transform.inverseTransform(pixel, new Point())
		(res.getX.toInt + 1, res.getY.toInt + 1)
	}
	override def paint(g: Graphics2D){
		val curTransf = transform
		g.drawImage(image, curTransf, null)
		if(showRoi) {
			g.setColor(java.awt.Color.YELLOW)
			g.draw(roirect)
		}
		if(showDetectedMols){
			val centerPoints = molsToShow.map{m => 
				curTransf.transform(new Point(m._1 + .5, m._2 + .5), new Point())
			}
			g.setColor(java.awt.Color.RED)
			val radX = curTransf.getScaleX * Main.pars.ImRad
			val radY = curTransf.getScaleY * Main.pars.ImRad
			centerPoints.foreach{pnt =>
				g.draw(new Ellipse(pnt.getX - radX, pnt.getY - radY, radX * 2, radY * 2))
			}
		}
	}
	
	listenTo(mouse.clicks)
	reactions += {
		case down: event.MousePressed  =>
		  	val (x, y) = point2ints(down.point)
			roirect.setBounds(x, y, 0, 0)
			state ! "selectroi"
			//println (down)
		case drag: event.MouseDragged =>
		  	val (x, y) = point2ints(drag.point)
		  	roirect.add(x, y)
		  	repaint
		  	//println(drag)
		case up: event.MouseReleased =>
		  	if(point2ints(up.point) == point2ints(roirect.getLocation)){
		  		state ! "noroi"
		  		showRoi = false
		  		repaint
		  	} else {state ! roirect}
		  	state ! "finishselectingroi"
		  	//println(up)
	}
}