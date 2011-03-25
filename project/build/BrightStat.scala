import sbt._

class SamplesProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject{

	val swing =  "org.scala-lang" % "scala-swing" % "2.8.1"
	val scalaTest = "org.scalatest" % "scalatest" % "1.3"
	val junit = "junit" % "junit" % "4.8.2"

	override def fork = forkRun
}
