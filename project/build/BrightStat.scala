import sbt._

class SamplesProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject{

	val swing =  "org.scala-lang" % "scala-swing" % "2.9.0"
	val scalaTest = "org.scalatest" % "scalatest_2.9.0" % "1.4.1"
	val junit = "junit" % "junit" % "4.8.2"

	override def fork = forkRun
}
