import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

  //val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  //val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.4.0"

  val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
  val assemblySBT = "com.codahale" % "assembly-sbt" % "0.1.1"
  
  val proguard = "org.scala-tools.sbt" % "sbt-proguard-plugin" % "0.0.5"

}

