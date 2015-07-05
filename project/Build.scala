import sbt._
import Keys._

object ApplicationBuild extends Build with meta.Properties {
	val scalaVersions = Seq("2.10.5", "2.11.6")
	
	lazy val root = (project in file(".")).settings(
		name := "iso-macro",
		scalacOptions ++= scalaOptionsVersion(
			scalaVersion.value, flags210 = Seq("-Yfundep-materialization"), Nil
		),
		libraryDependencies +=
			"org.scala-lang" % "scala-reflect" % scalaVersion.value
	)
	lazy val bindings = (project in file("bindings")).settings(
		name := "iso-bindings",
		organization in ThisBuild := "godenji",
		version in ThisBuild := isoMacrosVersion,
		crossScalaVersions := scalaVersions,
		scalaVersion in ThisBuild := scalaVersions.head,
		scalacOptions ++= scalaOptionsVersion(
			scalaVersion.value, flags210 = Seq("-Xdivergence211"), Nil
		),
		mappings in (Compile, packageBin) ++= mappings.in(root, Compile, packageBin).value,
		mappings in (Compile, packageSrc) ++= mappings.in(root, Compile, packageSrc).value
	).enablePlugins(play.sbt.PlayScala).dependsOn(root).aggregate(root)
	
	// provide scalac flag(s) based on Scala version
	def scalaOptionsVersion(
		scalaVersion: String, flags210: Seq[String], flags211: Seq[String]
	) =
		(CrossVersion.partialVersion(scalaVersion) match {
			case Some((2, scalaMajor)) if scalaMajor == 10 => flags210
			case Some((2, scalaMajor)) if scalaMajor == 11 => flags211
			case _=> Nil
		})
}
