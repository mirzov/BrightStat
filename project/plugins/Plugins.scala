import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

  val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

  val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.4.0"
  
  val proguard = "org.scala-tools.sbt" % "sbt-proguard-plugin" % "0.0.5"

}

