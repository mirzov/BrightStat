package se.lu.chemphys.sms.brightstat.ui

import se.lu.chemphys.sms.spe.MovieFromSpeFile
import java.io.File
import se.lu.chemphys.sms.spe.Movie
import scala.swing._
import scala.collection.mutable.Seq
import se.lu.chemphys.sms.spe.MovieFromFrames
import se.lu.chemphys.sms.brightstat.DefaultBrightStat
import se.lu.chemphys.sms.brightstat.BrightStatSaver

class BrightStatMenuBar extends MenuBar with StatefulUiComponent{
	
	private var openDir: File = null//new File("/home/oleg/Documents/ChemPhys/BrightStat/tests")
	
	private val openAction = Action("Open file"){
		val chooser = new FileChooser(openDir){
			fileSelectionMode = FileChooser.SelectionMode.FilesOnly
			multiSelectionEnabled = true
		}
		val res = chooser.showOpenDialog(this)
		if(res == FileChooser.Result.Approve){
			val files = chooser.selectedFiles
			openDir = files.head.getParentFile
			files.length match{
			  	case 1 =>
					Main.movie = new MovieFromSpeFile(chooser.selectedFile.getAbsolutePath)
					Main.movieFile = Some(chooser.selectedFile)
					//Main.movie.brightStat = Some(new DefaultBrightStat(chooser.selectedFile))
			  	case _ =>
			  	  	val dialog = new BatchProcessingDialog(Main.pars, files, Main.top)
			  	  	dialog.centerOnScreen()
			  	  	dialog.open()
			}
		}
	}
	val openItem = new MenuItem(openAction)

	private val saveAction = Action("Resave processing results"){
	  for(bs <- Main.movie.brightStat){
	    val molsShow = Main.movieWidget.movieScreen.molsToShow
	    val toRemove = (0 to molsShow.length - 1).toArray.filter(!molsShow(_).selected)
	    val nMolsOriginal = bs.nMolecules
	    toRemove.length match{
	      case 0 => Dialog.showMessage(Main.movieWidget.movieScreen, "All the molecules are selected, resaving is pointless", "Resaving not done")
	      case `nMolsOriginal` => Dialog.showMessage(Main.movieWidget.movieScreen, "No molecules are selected, resaving would destroy all information", "Resaving not done")
	      case _ =>
		    bs.removeMolecules(toRemove)
		    molsShow.keepOnlySelected()
		    val saver = new BrightStatSaver(bs, Main.movieFile)
		    saver.rewriteMoleculeReports()
		    Main.movieWidget.movieScreen.repaint()
	    }
	  }
	}
	val saveItem = new MenuItem(saveAction)
	
	private val quitAction = Action("Quit") {Main.state ! "quit"}
	val quitItem = new MenuItem(quitAction)
	contents += new Menu("File"){
		contents += (openItem, saveItem, quitItem)
	}
	
//	private val parsDialog = new PParsDialog(Main.top)
	
	private val prefAction = Action("Preferences") {
		val dialog = new PParsDialog(Main.top)
		dialog.centerOnScreen()
		dialog.open()
	}
	val prefItem = new MenuItem(prefAction)
	private val settAction = Action("Settings") {
		val dialog = new SettingsDialog(Main.top)
		dialog.centerOnScreen()
		dialog.open()
	}
	val settItem = new MenuItem(settAction)
	contents += new Menu("Edit"){
		contents += prefItem
		contents += settItem
	}
	
	private val sumUpAction = Action("Sum the frames up"){
		val sumFrame = Main.movie.sumUpFrames
		val sumMovie = new MovieFromFrames(IndexedSeq(sumFrame))
		Main.movie = sumMovie
	}
	val sumItem = new MenuItem(sumUpAction)
	
	private val detectMolsAction = Action("Detect molecules on this frame"){
		val frame = Main.movie.getFrame(Main.movieWidget.currentFrame)
		val maxs = frame.detectLocalMaxs(Main.pars)
		val mols = frame.detectMolecules(maxs, Main.pars)
		val screen = Main.movieWidget.movieScreen
		screen.molsToShow = screen.molsToShow.replaceCoordinates(mols)
		screen.showDetectedMols = true
		screen.repaint()
	}
	val detectMolsItem = new MenuItem(detectMolsAction)
	
	contents += new Menu("Calculate"){
		contents += sumItem
		contents += detectMolsItem
	}
	
	private val deselectAllAction = Action("Deselect all molecules"){
	  Main.movieWidget.movieScreen.molsToShow.deselectAll()
	  Main.movieWidget.movieScreen.repaint()
	}
	
	private val selectAllAction = Action("Select all molecules"){
	  Main.movieWidget.movieScreen.molsToShow.selectAll()
	  Main.movieWidget.movieScreen.repaint()
	}
	
	contents += new Menu("Select"){
		contents += new MenuItem(selectAllAction)
		contents += new MenuItem(deselectAllAction)
	}
	
	override def toInitial(){
		saveItem.enabled = false
	}
	
	def toReady(){
		openItem.enabled = true
		prefItem.enabled = true
		sumItem.enabled = true
		detectMolsItem.enabled = true
		saveItem.enabled = Main.movie.brightStat match {
		  case Some(bStat) if(bStat.nMolecules > 0) => true
		  case _ => false
		}
	}
	def toProcessing(){
		openItem.enabled = false
		prefItem.enabled = false
		sumItem.enabled = false
		detectMolsItem.enabled = false
		saveItem.enabled = false
	}
	
	override def toRoiSelection(){
	  	saveItem.enabled = false
	}

	
}