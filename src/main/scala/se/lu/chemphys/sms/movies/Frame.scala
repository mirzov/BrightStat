package se.lu.chemphys.sms.movies

trait Frame[T] {
	val XDim: Int
	val YDim: Int
	def min: T
	def max: T
	def sum: T
	def average: Double
	def apply(i: Int, j: Int): Int
	def update(i: Int, j: Int, x: T): Unit
	def isLocalMax(i: Int, j: Int): Boolean = {
		i > 0 && j > 0 && i < XDim - 1 && j < YDim - 1  && {
			var i0 = -1;
			while(i0 <= 1) {
				for(int j0=-1;j0<=1;j0++){
					if (ImageData[j*XDim+i]<=ImageData[(j+j0)*XDim+i+i0] && (i0||j0)) return false;
				}
				
			}
		}
	}
}