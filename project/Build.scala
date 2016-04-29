import sbt._
import Keys._

import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin.autoImport._

object ApplicationBuild extends Build {
	val scalaCurrent = "2.11.8" // "2.12.0"
	val scalaCrossVersions = Seq(/* "2.11.8", */ scalaCurrent)
	/*
	 * NOTE: both root project and cross project
	 * 	share the same source tree; however,
	 * 	they BOTH need to be published in order for:
	 * 	1) bindings project to pull in "value-class-root" dep
	 * 	2) js/jvm apps to pull in "value-class-isomorphism" dep
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
    crossProject.crossType(CrossType.Pure).in(file(".")).
    settings(
  		name := "value-class-isomorphism",
  		organization in ThisBuild := "godenji",
			version in ThisBuild := "0.1.1",
			crossScalaVersions in ThisBuild := scalaCrossVersions,
			scalaVersion in ThisBuild := scalaCurrent,
	    //EclipseKeys.useProjectId := true,
			libraryDependencies +=
				"org.scala-lang" % "scala-reflect" % scalaVersion.value
		)

	lazy val bindings =
		project.in(file("bindings")).settings(
			name := "value-class-bindable",
			scalaVersion := scalaCurrent
		).
		enablePlugins(play.sbt.PlayScala).dependsOn(root).aggregate(root)
}
