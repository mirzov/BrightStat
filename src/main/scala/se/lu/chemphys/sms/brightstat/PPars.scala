package se.lu.chemphys.sms.brightstat

import se.lu.chemphys.sms.spe.Frame

class PPars {
	var ImRad = 3f
	var SmRad = 6f
	var BrightNum = 8
	var BrightSize = 2.24f
	var NofStartFrames = 3
	var CutOff = 0.2f
	var NoiseSigms = 3f
	var Correlation = 0.6f
	var UseExProfile = false
	var Normalize = false
	var UseROI = false
	var ExFrame: Frame[Double] = _
	var roi: ROI = NoROI
	
	def withinImRange(dx: Int, dy: Int): Boolean = {
		dx * dx + dy * dy <= ImRad * ImRad
	}
	def withinImRange(pix1: (Int, Int), pix2: (Int, Int)): Boolean =
			withinImRange(pix1._1 - pix2._1, pix1._2 - pix2._2)
	
	def withinSmRange(dx: Int, dy: Int): Boolean = {
		dx * dx + dy * dy <= SmRad * SmRad
	}
	
	def getNumericValue(varName: String): String = varName match{
		case "ImRad" => ImRad.toString
		case "SmRad" => SmRad.toString
		case "BrightNum" => BrightNum.toString
		case "BrightSize" => BrightSize.toString
		case "NofStartFrames" => NofStartFrames.toString
		case "CutOff" => CutOff.toString
		case "NoiseSigms" => NoiseSigms.toString
		case "Correlation" => Correlation.toString
		case _ => throw new IllegalArgumentException("No such numeric variable in PPars: " + varName)
	}
	
	def getBooleanValue(varName: String): Boolean = varName match{
		case "Normalize" => Normalize
		case "UseExProfile" => UseExProfile
		case "UseROI" => UseROI
		case _ => throw new IllegalArgumentException("No such boolean variable in PPars: " + varName)
	}
	
	def setNumericValue(varName: String, value: String) = varName match {
		case "ImRad" => ImRad = value.toFloat
		case "SmRad" => SmRad = value.toFloat
		case "BrightNum" => BrightNum = value.toInt
		case "BrightSize" => BrightSize = value.toFloat
		case "NofStartFrames" => NofStartFrames = value.toInt
		case "CutOff" => CutOff = value.toFloat
		case "NoiseSigms" => NoiseSigms = value.toFloat
		case "Correlation" => Correlation = value.toFloat
		case _ => throw new IllegalArgumentException("No such numeric variable in PPars: " + varName)
	}

	def setBooleanValue(varName: String, value: Boolean) = varName match{
		case "Normalize" => Normalize = value
		case "UseExProfile" => UseExProfile = value
		case "UseROI" => UseROI = value
		case _ => throw new IllegalArgumentException("No such boolean variable in PPars: " + varName)
	}
	
}