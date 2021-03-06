import sbt._
import Keys._
import PlayProject._

object PluginBuild extends Build {

  val moduleOrganization = "info.schleichardt"
  val appName = "play-2-basic-auth"
  val appVersion = "0.2-SNAPSHOT"
  val publishingFolder = Path.userHome.absolutePath + "/Projekte/schleichardt.github.com/jvmrepo"

  val appDependencies = Seq(
  )

  val main = PlayProject(appName, appVersion, appDependencies).settings(
    organization := moduleOrganization,
    publishTo := Some(Resolver.file("file", new File(publishingFolder))(Resolver.mavenStylePatterns)),
    publishMavenStyle := true,
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6")//for compatibility with Debian Squeeze
  )

}
