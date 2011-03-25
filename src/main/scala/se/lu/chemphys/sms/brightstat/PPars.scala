package se.lu.chemphys.sms.brightstat

import se.lu.chemphys.sms.spe.Frame

sealed trait ROI{
	def isInRoi(i: Int, j: Int): Boolean
}

class DefaultROI(left: Int, top: Int, right: Int, bottom: Int) extends ROI{
	override def isInRoi(i: Int, j: Int) = {
		i >= left && i <= right && j >= top && j <= bottom
	}
}

object NoROI extends ROI{
	override def isInRoi(i: Int, j: Int) = true
}

object PPars {
	
	var ImRad = 3
	var SmRad = 6
	var BrightNum = 8
	var BrightSize = 2.24
	var NofStartFrames = 1
	var CutOff = 0.2
	var NoiseSigms = 3
	var Correlation = 0.6
	var UseExProfile = false
	var Normalize = false
	var UseROI = false
	var ExFrame: Frame[_] = _
	var roi: ROI = NoROI
	
	
}