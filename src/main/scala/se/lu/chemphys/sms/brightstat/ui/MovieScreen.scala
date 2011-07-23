package se.lu.chemphys.sms.brightstat.ui
import java.awt.geom.Ellipse2D.{Double => Ellipse}
import java.awt.geom.Point2D.{Double => Point}
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.Graphics2D
import java.awt.Rectangle

import scala.swing.BorderPanel
import scala.swing.event

import MovieScreen.lookup
import MovieScreen.rectToROI
import MovieScreen.roiToRect
import MovieWidget.point2ints
import se.lu.chemphys.sms.brightstat.NoROI
import se.lu.chemphys.sms.brightstat.ROI

object MovieScreen{

	def lookup(pixel: Point2D, transform: AffineTransform): (Int, Int) = {
		val res = transform.inverseTransform(pixel, new Point())
		(res.getX.toInt, res.getY.toInt)
	}

	def rectToROI(rect: Rectangle, transform: AffineTransform): ROI = {
		val leftTop = rect.getLocation
		val (left, top) = lookup(leftTop, transform)
		val rightBottom = new Point(leftTop.getX + rect.getWidth, leftTop.getY + rect.getHeight)
		val (right, bottom) = lookup(rightBottom, transform)
		ROI(left, top, right, bottom)
	}
	
	def roiToRect(roi: ROI, transform: AffineTransform): Rectangle = {
		val leftTop = transform.transform(new Point(roi.left, roi.top), new Point())
		val (left, top) = (leftTop.getX.toInt, leftTop.getY.toInt)
		val rightBottom = transform.transform(new Point(roi.right, roi.bottom), new Point())
		val (right, bottom) = (rightBottom.getX.toInt, rightBottom.getY.toInt)
		new Rectangle(left, top, right-left, bottom - top)
	}
}

class MovieScreen(widget: MovieWidget, state: StateManager) extends BorderPanel {
	import MovieWidget._
	import MovieScreen._
	import widget._

	var molsToShow: Seq[(Int, Int)] = Array[(Int, Int)]()
	var showDetectedMols = false
	
	def transform : AffineTransform = {
		val scaleX = size.width.toDouble / image.getWidth
		val scaleY = size.height.toDouble / image.getHeight
		new AffineTransform(scaleX, 0, 0, scaleY, 0, 0)
	}
	
	def toCameraPixel(pixel: Point2D): (Int, Int) = lookup(pixel, transform)
	
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
	
	private val roirect = new java.awt.Rectangle()
	private var lastRoi: ROI = NoROI
	private def getRoi: ROI = {
		lastRoi = rectToROI(roirect, transform); lastRoi
	}
	
	listenTo(mouse.clicks, this)
	reactions += {
		case down: event.MousePressed  =>
		  	val (x, y) = point2ints(down.point)
			roirect.setBounds(x, y, 0, 0)
			state ! "selectroi"
		case drag: event.MouseDragged =>
		  	val (x, y) = point2ints(drag.point)
		  	roirect.add(x, y)
		  	repaint()
		case up: event.MouseReleased =>
		  	if(point2ints(up.point) == point2ints(roirect.getLocation)){
		  		state ! "noroi"
		  		showRoi = false
		  		repaint()
		  	} else {state ! getRoi}
		  	state ! "finishselectingroi"
		case resize: event.UIElementResized if(lastRoi != NoROI) =>
			roirect.setBounds(roiToRect(lastRoi, transform))
			repaint()
	}
}