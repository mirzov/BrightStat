package se.lu.chemphys.sms.brightstat.ui

import se.lu.chemphys.sms.spe.Utils
import se.lu.chemphys.sms.brightstat.PPars
import se.lu.chemphys.sms.brightstat.BrightStat
import scala.swing.SwingWorker
import scala.actors.Actor

class BrightStatCalculator(parent: Actor, pars: PPars, callBack: Int => Unit) extends SwingWorker{
  
	def act(){
		val brightStat = new BrightStat
		var molStats = Main.movie.detectMoleculesFromScratch(pars, cancelled, callBack)
		var f = pars.startFrame + pars.NofStartFrames - 1
		brightStat.addMolStats(molStats, f)
		
		val startFrameMolStats = molStats
		while (f > pars.startFrame && !cancelled){
			f -= 1
			callBack(f)
			val frame = Main.movie.getFrame(f) 
			molStats = frame.followMolecules(molStats, pars)
			brightStat.addMolStats(molStats, f)
		}
		molStats = startFrameMolStats
		f = pars.startFrame + pars.NofStartFrames
		while (f <= Main.movie.Nframes && !cancelled){
			callBack(f)
			molStats = Main.movie.getFrame(f).followMolecules(molStats, pars)
			brightStat.addMolStats(molStats, f)
			f += 1
		}
		
		if (!cancelled) parent ! brightStat
	}
}