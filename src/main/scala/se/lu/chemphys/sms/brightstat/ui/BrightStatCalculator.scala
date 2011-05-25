package se.lu.chemphys.sms.brightstat.ui

import se.lu.chemphys.sms.spe.{Utils, Frame}
import se.lu.chemphys.sms.brightstat.PPars
import se.lu.chemphys.sms.brightstat.BrightStat
import scala.swing.SwingWorker
import scala.actors.Actor
import se.lu.chemphys.sms.brightstat.NoROI

class BrightStatCalculator(parent: Actor, pars: PPars, callBack: Int => Unit) extends SwingWorker{
  
	def act(){
		val brightStat = new BrightStat
		var molStats = Main.movie.detectMoleculesFromScratch(pars, cancelled, callBack)
		var f = pars.startFrame + pars.NofStartFrames - 1
		brightStat.addMolStats(molStats, f)
		
		def processFrame(f: Int){
			callBack(f)
			val frame = Main.movie.getFrame(f)
			molStats = frame.followMolecules(molStats, pars)
			brightStat.addMolStats(molStats, f)
			if(pars.exRoi != NoROI){
				val sig = frame.calcSumInRoi(pars.exRoi)
				brightStat.addExSignal(sig, f)
			}
			if(pars.emRoi != NoROI){
				val sig = frame.calcSumInRoi(pars.emRoi)
				brightStat.addEmSignal(sig, f)
			}
		}
		
		val startFrameMolStats = molStats
		while (f > pars.startFrame && !cancelled){
			f -= 1
			processFrame(f)
		}
		molStats = startFrameMolStats
		f = pars.startFrame + pars.NofStartFrames
		while (f <= Main.movie.Nframes && !cancelled){
			processFrame(f)
			f += 1
		}
		
		if (!cancelled) parent ! brightStat
	}
}