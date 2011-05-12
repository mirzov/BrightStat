package se.lu.chemphys.sms.brightstat

class Marks(val XDim: Int, val YDim: Int) {
	
	private val pixels = new Array[Byte](XDim * YDim)
	
	def isEmpty(x: Int, y: Int): Boolean = (pixels(y * XDim + x) == 0)
	def makeEmpty(x: Int, y: Int){
		pixels(y * XDim + x) = 0
	}
	
	def isMolecule(x: Int, y: Int): Boolean = (pixels(y * XDim + x) & 0x01) != 0
	def makeMolecule(x: Int, y: Int){
		pixels(y * XDim + x) = (pixels(y * XDim + x) | 0x01).toByte
	}
	
	def isBright(x: Int, y: Int): Boolean = (pixels(y * XDim + x) & 0x02) != 0
	def makeBright(x: Int, y: Int){
		pixels(y * XDim + x) = (pixels(y * XDim + x) | 0x02).toByte
	}
	
	def markMolecule(x: Int, y: Int, pars: PPars){
		val imRad = scala.math.ceil(pars.ImRad).toInt
		for(
			i <- (x - imRad) to (x + imRad);
			j <- (y - imRad) to (y + imRad)
            if pars.withinImRange(i - x, j - y)
        ) makeMolecule(i, j);
	}
}