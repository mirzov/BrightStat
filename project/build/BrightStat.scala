import sbt._

class SamplesProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject with ProguardProject{

	val swing =  "org.scala-lang" % "scala-swing" % "2.9.0"
	val scalaTest = "org.scalatest" % "scalatest_2.9.0" % "1.4.1"
	val junit = "junit" % "junit" % "4.8.2"
	
	override def proguardInJars = super.proguardInJars +++ scalaLibraryPath
	override def proguardOptions = List(
		"-keep class se.lu.chemphys.sms.brightstat.*",
		"-keep class se.lu.chemphys.sms.brightstat.ui.*",
		"-keep class se.lu.chemphys.sms.spe.*",
		//proguardKeepAllScala,
		proguardKeepMain("se.lu.chemphys.sms.brightstat.ui.Main")
	)

	override def fork = forkRun
}
