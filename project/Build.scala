import sbt._
import Keys._

import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin.autoImport._

object ApplicationBuild extends Build {
	val scalaVersions = Seq("2.10.6", "2.11.8") // "2.12.0"
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
			version in ThisBuild := "0.1.0",
			crossScalaVersions in ThisBuild := scalaVersions,
			scalaVersion := scalaVersions.head,
			scalacOptions ++= (
				if(scalaVersion.value startsWith "2.10.") Seq(
					"-Yfundep-materialization"
				) else Nil
      ),
	    //EclipseKeys.useProjectId := true,
			libraryDependencies +=
				"org.scala-lang" % "scala-reflect" % scalaVersion.value
		)

	lazy val bindings =
		project.in(file("bindings")).settings(
			name := "value-class-bindable",
			scalaVersion := scalaVersions.tail.head,
			scalacOptions ++= (
				if(scalaVersion.value startsWith "2.10.") Seq(
					"-Xdivergence211"
				) else Nil
      )
		).
		enablePlugins(play.sbt.PlayScala).dependsOn(root).aggregate(root)
}
