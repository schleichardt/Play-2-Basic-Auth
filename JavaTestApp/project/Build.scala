import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "JavaTestApp"
    val appVersion      = "0.2-SNAPSHOT"

    val appDependencies = Seq(
      "info.schleichardt" %% "play-2-basic-auth" % appVersion
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
