package se.lu.chemphys.sms.brightstat

sealed trait ROI{
	val left: Int
	val top: Int
	val right: Int
	val bottom: Int
	def isInRoi(i: Int, j: Int): Boolean
	def intersect(that: ROI) = {
		val il = that.left.max(left)
		val it = that.top.max(top)
		val ir = that.right.min(right)
		val ib = that.bottom.min(bottom)
		ROI(il, it, ir, ib)
	}
}

object ROI{
	def apply(left: Int, top: Int, right: Int, bottom: Int) = 
		new DefaultROI(left, top, right, bottom)
	def apply() = NoROI
}

class DefaultROI(val left: Int, val top: Int, val right: Int, val bottom: Int) extends ROI{
	override def isInRoi(i: Int, j: Int) = {
		i >= left && i <= right && j >= top && j <= bottom
	}
}

object NoROI extends ROI{
	override def isInRoi(i: Int, j: Int) = true
	val left = Int.MinValue
	val right = Int.MaxValue
	val top = Int.MinValue
	val bottom = Int.MaxValue
}
