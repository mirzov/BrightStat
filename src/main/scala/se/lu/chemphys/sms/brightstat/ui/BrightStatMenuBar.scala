package se.lu.chemphys.sms.brightstat.ui

import se.lu.chemphys.sms.spe.MovieFromSpeFile
import java.io.File
import se.lu.chemphys.sms.spe.Movie
import scala.swing._
import scala.collection.mutable.Seq
import se.lu.chemphys.sms.spe.MovieFromFrames

class BrightStatMenuBar extends MenuBar with StatefulUiComponent{
	
	private val quitAction = Action("Quit") {Main.state ! "quit"}
	
	private var openDir: File = null
	
	private val openAction = Action("Open file"){
		val chooser = new FileChooser(openDir){
			fileSelectionMode = FileChooser.SelectionMode.FilesOnly
			multiSelectionEnabled = false
		}
		val res = chooser.showOpenDialog(this)
		if(res == FileChooser.Result.Approve){
			openDir = chooser.selectedFile.getParentFile
			Main.movie = new MovieFromSpeFile(chooser.selectedFile.getAbsolutePath)
			Main.movieFile = chooser.selectedFile
		}
	}
	
	val openItem = new MenuItem(openAction)
	val quitItem = new MenuItem(quitAction)
	contents += new Menu("File"){
		contents += (openItem, quitItem)
	}
	
//	private val parsDialog = new PParsDialog(Main.top)
	
	private val prefAction = Action("Preferences") {
		val dialog = new PParsDialog(Main.top)
		dialog.centerOnScreen
		dialog.open
	}
	val prefItem = new MenuItem(prefAction)
	contents += new Menu("Edit"){
		contents += prefItem
	}
	
	private val sumUpAction = Action("Sum the frames up"){
		val sumFrame = Main.movie.sumUpFrames
		val sumMovie = new MovieFromFrames(IndexedSeq(sumFrame))
		Main.movie = sumMovie
	}
	val sumItem = new MenuItem(sumUpAction)
	contents += new Menu("Calculate"){
		contents += sumItem
	}
	
	def toReady(){
		openItem.enabled = true
		prefItem.enabled = true
		sumItem.enabled = true
	}
	def toProcessing(){
		openItem.enabled = false
		prefItem.enabled = false
		sumItem.enabled = false
	}

	
}