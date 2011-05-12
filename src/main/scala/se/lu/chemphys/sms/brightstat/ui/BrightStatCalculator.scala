package se.lu.chemphys.sms.brightstat.ui

import se.lu.chemphys.sms.brightstat.BrightStat
import scala.swing.SwingWorker
import Main._
class BrightStatCalculator extends SwingWorker{
	def act(){
		val brightStat = new BrightStat
		var molStats = movie.detectMoleculesFromScratch(currentFrame, pars)
		var f = currentFrame + pars.NofStartFrames
		brightStat.addMolStats(molStats, f)
		
		while (f > currentFrame){
			f -= 1
			molStats = movie.getFrame(f).followMolecules(molStats, pars)
			brightStat.addMolStats(molStats, f)
		}
		
		f = currentFrame + pars.NofStartFrames
		while (f < movie.Nframes ){
			f += 1
			molStats = movie.getFrame(f).followMolecules(molStats, pars)
			brightStat.addMolStats(molStats, f)
		}
		
		brightStat.printIntensityReport(System.out)
		
	}
}