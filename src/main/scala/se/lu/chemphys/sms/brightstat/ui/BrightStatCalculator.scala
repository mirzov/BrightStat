package se.lu.chemphys.sms.brightstat.ui

import se.lu.chemphys.sms.spe.{Utils, Frame}
import se.lu.chemphys.sms.brightstat._
import scala.swing.SwingWorker
import scala.actors.Actor
import se.lu.chemphys.sms.spe.Movie

class BrightStatCalculator(movie: Movie, pars: PPars, progressCallBack: Int => Unit, finishCallBack: BrightStat => Unit) extends SwingWorker{
  
	def act(){
		val brightStat = new DefaultBrightStat()
		var molStats = movie.detectMoleculesFromScratch(pars, cancelled, progressCallBack)
		var f = pars.startFrame + pars.NofStartFrames - 1
		brightStat.addMolStats(molStats, f)
		addExSignal(f);	addEmSignal(f)
		
		def addExSignal(f: Int) =
			if(pars.exRoi != NoROI){
				val sig = movie.getFrame(f).calcSumInRoi(pars.exRoi)
				brightStat.addExSignal(sig, f)
			}
		  
		def addEmSignal(f: Int) = 
		  if(pars.emRoi != NoROI){
				val sig = movie.getFrame(f).calcSumInRoi(pars.emRoi)
				brightStat.addEmSignal(sig, f)
			}
		
		def processFrame(f: Int){
			progressCallBack(f)
			molStats = movie.getFrame(f).followMolecules(molStats, pars)
			brightStat.addMolStats(molStats, f)
			addExSignal(f); addEmSignal(f)
		}
		
		val startFrameMolStats = molStats
		while (f > pars.startFrame && !cancelled){
			f -= 1
			processFrame(f)
		}
		molStats = startFrameMolStats
		f = pars.startFrame + pars.NofStartFrames
		while (f <= movie.Nframes && !cancelled){
			processFrame(f)
			f += 1
		}
		
		if (!cancelled) finishCallBack(brightStat)
	}
}