import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "TestApp"
  val appVersion = "0.2-SNAPSHOT"

  val appDependencies = Seq(
    "info.schleichardt" %% "play-2-basic-auth" % appVersion
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // Add your own project settings here
  )

}
