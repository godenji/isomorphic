import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import ApplicationBuild._

val scalaCrossVersions = Seq(/* "2.11.8", */ scalaRelease)
/*
 * NOTE: both root project and cross project
 *  share the same source tree; however,
 *  they BOTH need to be published in order for:
 *  1) bindings project to pull in "value-class-root" dep
 *  2) js/jvm apps to pull in "value-class-isomorphism" dep
 */
lazy val root =
  project.in(file(".")).settings(
    name := "value-class-root"
    //publish := {}, publishLocal := {}
  ).
  dependsOn(sharedJvm, sharedJs).
  aggregate(sharedJvm, sharedJs)

lazy val sharedJvm = shared.jvm
lazy val sharedJs  = shared.js
lazy val shared =
  crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file(".")).
  settings(
    name := appName,
    organization in ThisBuild := "godenji",
    version in ThisBuild := appVersion,
    crossScalaVersions in ThisBuild := scalaCrossVersions,
    scalaVersion in ThisBuild := scalaRelease,
    //EclipseKeys.useProjectId := true,
    libraryDependencies +=
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
  )

lazy val bindings =
  project.in(file("bindings")).settings(
    name := "value-class-bindable",
    scalaVersion := scalaRelease,
    libraryDependencies += Libs.scalaTestPlay
  ).
  enablePlugins(play.sbt.PlayScala).dependsOn(root).aggregate(root)
