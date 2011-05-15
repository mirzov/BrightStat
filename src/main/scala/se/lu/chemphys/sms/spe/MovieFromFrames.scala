package se.lu.chemphys.sms.spe

class MovieFromFrames(frames: IndexedSeq[Frame[_]]) extends Movie {
	
	val Nframes: Int = frames.size
	
	assert(Nframes > 0, "The number of frames in a movie must be positive!")
	
	val XDim: Int = frames(0).XDim
	val YDim: Int = frames(0).YDim
	
	def getFrame(n: Int) = {
		assert(n >= 1 && n <= Nframes, "Requested frame number is out of bounds.")
		frames(n - 1)
	}
}