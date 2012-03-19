package se.lu.chemphys.sms.spe

import scala.collection.mutable.ArrayBuffer
import se.lu.chemphys.sms.brightstat.MolStat
import se.lu.chemphys.sms.brightstat.PPars
import se.lu.chemphys.sms.brightstat.BrightStat

trait Movie{
	val Nframes: Int
	val XDim: Int
	val YDim: Int
	def getFrame(n: Int): Frame[_]
	var brightStat: Option[BrightStat] = None
	
	def sumUpFrames: Frame[Double] = {
		val sumAccum = new Array[Double](XDim * YDim)
		for(f <- 1 to Nframes){
			val frame = getFrame(f)
			frame.addDataToArray(sumAccum)
		}
		new Frame(XDim, YDim, sumAccum)
	}
	
	def detectMoleculesFromScratch(pars: PPars, isCancelled: => Boolean = false,
							callBack: (Int => Unit) = (i => ()) ): Array[MolStat] = {

		def areSame(mol1: (Int, Int), mol2: (Int, Int)): Boolean = 
			pars.withinImRange(mol1._1 - mol2._1, mol1._2 - mol2._2)
			
		var frame = getFrame(pars.startFrame)
		var maxs = frame.detectLocalMaxs(pars)
		var mols = frame.detectMolecules(maxs, pars).map{pix => (pix, 0)}
		var f = pars.startFrame + 1
		val endFrame = (pars.startFrame + pars.NofStartFrames - 1).min(Nframes).toInt
		while(f <= endFrame && ! isCancelled){
			callBack(f)
			frame = getFrame(f)
			//println("Switching to frame " + f)
			val newMols = ArrayBuffer[((Int, Int), Int)]()
			for((pix, occurence) <- mols){
				val newPix = frame.shiftToLocalMax(pix)
				if(areSame(pix, newPix) && frame.isMolecule(newPix, pars)) {
					//println("Confirmed mol " + pix + " in the spot " + newPix)
					newMols += ((newPix, occurence + 1))
				}
				else if(occurence > 0) {
					newMols += ((pix, occurence))
					//println("Kept previously confirmed mol " + pix)
				}
			}
			maxs = frame.detectLocalMaxs(pars)
			val freshmols = frame.detectMolecules(maxs, pars)
			for(freshmol <- freshmols if !newMols.exists(mol => areSame(mol._1, freshmol))){
				newMols += ((freshmol, 0))
				//println("Added new mol " + freshmol)
			}
			mols = newMols
			f += 1
		}
		val molPixels = mols.filter(_._2 > 0).map(_._1)
		//println("Detected molPixels:" + molPixels.mkString(", "))
		//if(!isCancelled) frame.markBrightNonMolecules(maxs, pars)
		frame.calcSignals(molPixels, pars, true)
	}

}

