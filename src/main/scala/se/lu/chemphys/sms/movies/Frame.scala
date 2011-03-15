package se.lu.chemphys.sms.movies

trait Frame[T <: Ordered[T]] {
	val XDim: Int
	val YDim: Int
	def min: T
	def max: T
	def sum: T
	def average: Double
	def apply(i: Int, j: Int): T
	def update(i: Int, j: Int, x: T): Unit
	def isLocalMax(i: Int, j: Int): Boolean = {
		i > 0 && j > 0 && i < XDim - 1 && j < YDim - 1  && {
			var i0 = -1;
			while((i0 <= 1)) {
				var j0 = -1;
				while((j0 <= 1)){
					if (((i0 | j0) != 0) && (this(i, j) <= this(i + i0, j + j0))) return false;
					j0 = j0 + 1
				}
				i0 = i0 + 1
			}
			true
		}
	}
}