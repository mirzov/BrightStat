import AssemblyKeys._

name := "BrightStat"

version := "2.0"

libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "+" % "test",
	"org.scala-lang" % "scala-swing" % "2.9.1",
	"junit" % "junit" % "4.8.2"
)

seq(assemblySettings: _*)

fork in run := true

parallelExecution in Test := false
