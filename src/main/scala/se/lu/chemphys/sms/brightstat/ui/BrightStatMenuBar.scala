package se.lu.chemphys.sms.brightstat.ui

import se.lu.chemphys.sms.spe.MovieFromSpeFile
import java.io.File
import se.lu.chemphys.sms.spe.Movie
import scala.swing._
import scala.collection.mutable.Seq
import se.lu.chemphys.sms.spe.MovieFromFrames

class BrightStatMenuBar extends MenuBar{
	
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
	
	contents += new Menu("File"){
		contents += new MenuItem(openAction)
		contents += new MenuItem(quitAction)
	}
	
//	private val parsDialog = new PParsDialog(Main.top)
	
	private val prefAction = Action("Preferences") {
		val dialog = new PParsDialog(Main.top)
		dialog.centerOnScreen
		dialog.open
	}
	
	contents += new Menu("Edit"){
		contents += new MenuItem(prefAction)
	}
	
	private val sumUpAction = Action("Sum the frames up"){
		val sumFrame = Main.movie.sumUpFrames
		val sumMovie = new MovieFromFrames(IndexedSeq(sumFrame))
		Main.movie = sumMovie
	}
	
	contents += new Menu("Calculate"){
		contents += new MenuItem(sumUpAction)
	}
	
}