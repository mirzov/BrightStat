package se.lu.chemphys.sms.brightstat.ui

import java.io.File
import se.lu.chemphys.sms.spe.Movie
import scala.swing._
import scala.collection.mutable.Seq

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
			Main.movie = new Movie(chooser.selectedFile.getAbsolutePath)
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
		dialog.open//visible = true
//		parsDialog.open
//		if(!parsDialog.showing) println ("Not showing the dialog for some reason!")
	}
	
	contents += new Menu("Edit"){
		contents += new MenuItem(prefAction)
	}
	
}