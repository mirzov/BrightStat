package se.lu.chemphys.sms.brightstat.ui
import scala.swing._
import java.io.File
import se.lu.chemphys.sms.spe.MovieFromSpeFile
import se.lu.chemphys.sms.brightstat.PPars
import scala.collection.mutable.Buffer
import scala.actors.Actor
import scala.collection.mutable.Queue
import se.lu.chemphys.sms.brightstat.BrightStat
import se.lu.chemphys.sms.brightstat.BrightStatSaver
import se.lu.chemphys.sms.spe.Movie
import scala.swing.event.UIElementShown

class BatchProcessingDialog(pars: PPars, files: Seq[File], owner: Window) extends Dialog(owner) {
	modal = true
	title = "Batch BrightStat processing"
	
	private val activeCalcs = Buffer[SwingWorker]()
	private val fileQueue = Queue(files :_*)
	private val threads = 3
	private val manager = new BatchManager
	
	private val progressPanel = new BoxPanel(Orientation.Vertical)
	private val buttonPanel = new FlowPanel(FlowPanel.Alignment.Right)(
		new Button(Action("Abort!"){manager ! "cancel"})
	)
	
	
	def scheduleProcessing(file: File){
		val movie = new MovieFromSpeFile(file.getAbsolutePath)
		var calc: SwingWorker = null
		val progrBar = new BrightStatProgress(file, movie.Nframes, {
			calc.cancelled = true
			activeCalcs -= calc
			if(activeCalcs.isEmpty) manager ! "cancel"
		})
		calc = new BrightStatCalculator(movie, pars,
		    i => progrBar.progress = i,
		    bs => {
		    	progressPanel.contents -= progrBar
		    	progressPanel.revalidate()
		    	activeCalcs -= calc
		    	manager ! (bs, file)
		    }
		)
		progressPanel.contents += progrBar
		progressPanel.revalidate()
		activeCalcs += calc
		calc.start()
	}
	
	for(i <- 1 to threads) if(!fileQueue.isEmpty) scheduleProcessing(fileQueue.dequeue)

	contents = new BoxPanel(Orientation.Vertical){
		contents += progressPanel
		contents += buttonPanel 
	}
	
	manager.start()
	
	class BatchManager extends SwingWorker{
		def act(){
			loopWhile(!activeCalcs.isEmpty){
				react{
					case (bs: BrightStat, file: File) => 
						new BrightStatSaver(bs, file).save()
						if(!fileQueue.isEmpty) scheduleProcessing(fileQueue.dequeue)
						if(activeCalcs.isEmpty) close()
					case "cancel" => activeCalcs.foreach{_.cancelled = true}; close()
				}
			}
		}
	}
}


class BrightStatProgress(file: File, nFrames: Int, onCancel: => Unit) extends BoxPanel(Orientation.Horizontal){

	val progrBar = new ProgressBar{
		min = 1; max = nFrames
	}
	val label = new Label(file.getName)
	val cancButt = new Button(Action("Cancel!")(onCancel))
	
	contents ++= label :: progrBar :: cancButt :: Nil
	
	def progress_=(progress: Int){progrBar.value = progress} 
	def progress = progrBar.value
}

