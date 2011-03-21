package se.lu.chemphys.sms.brightstat;

import scala.swing.SwingWorker

class MovieShower extends SwingWorker{
	import Main._
	private val rnd = new java.util.Random
	private val meter = new PerformanceMeter
	meter.start()
	private var count = 0
	private val maxCount = 100

	def clearImage() {
		val array = new Array[Int](image.getHeight * image.getWidth)
		image.setRGB(0, 0, image.getWidth, image.getHeight, array, 0, image.getWidth)
		movieScreen.repaint
	}

	override def act{

		val totNumPix = image.getHeight * image.getWidth
		val array = new Array[Int](totNumPix)
		
		meter ! "reset"
		var frNum = 1
		while(!cancelled && frNum <= movie.Nframes){
			var frame = movie.getFrame(frNum)
			image = frame.getImage
			movieScreen.repaint
			frNum = frNum + 1
			//Thread.sleep(1)
		}
		Main.state ! "pause"
		meter ! movie.Nframes 
//		while(!cancelled){
//			var i = 0
//			while(i < totNumPix){
//				val r = rnd.nextInt(0xff)
//				val color = r << 16 | r << 8 | r
//				array(i) = color
//				i = i + 1
//			}
//			image.setRGB(0, 0, image.getWidth, image.getHeight, array, 0, image.getWidth)
//			movieScreen.repaint
//			count = count + 1
//			if(count == maxCount){
//				count = 0
//				meter ! maxCount
//			}
//			//Thread.sleep(1)
//		}
	}
}