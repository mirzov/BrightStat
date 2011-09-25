resolvers += Classpaths.typesafeResolver

resolvers += "Siasia repo" at "http://siasia.github.com/maven2"

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse" % "1.4.0")

libraryDependencies <+= (sbtVersion in update,scalaVersion) { (sbtV, scalaV) => Defaults.sbtPluginExtra("com.eed3si9n" % "sbt-assembly" % "0.6", "0.11.0-RC0", "2.9.1") }

//libraryDependencies <+= (sbtVersion in update,scalaVersion) { (sbtV, scalaV) => Defaults.sbtPluginExtra("com.github.siasia" % "xsbt-proguard-plugin" % "0.10.1", "", "2.8.1") }

