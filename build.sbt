import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val scalaRelease = "2.13.6"
val scalaCrossVersions = Seq("2.12.14", scalaRelease)
val appVersion = "0.1.8"

lazy val root =
  project.in(file(".")).settings(
    name := "isomorphic-root"
  ).
  dependsOn(sharedJvm, sharedJs).
  aggregate(sharedJvm, sharedJs)

lazy val sharedJvm = shared.jvm
lazy val sharedJs  = shared.js
lazy val shared =
  crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file(".")).
  settings(baseSettings("isomorphic")).
  settings(publishSettings("isomorphic")).
  enablePlugins(BuildInfoPlugin)

lazy val bindings =
  project.in(file("bindings")).settings(
    name := "isomorphic-play",
    libraryDependencies += scalaTestPlay
  ).
  settings(publishSettings("isomorphicPlay")).
  enablePlugins(play.sbt.PlayScala).
  dependsOn(sharedJvm, sharedJs).
  aggregate(sharedJvm, sharedJs)

lazy val rootSlick =
  project.in(file("isomorphic-slick")).
  settings(baseSettings("isomorphic-slick")).
  settings(publishSettings("isomorphicSlick")).
  settings(
    libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.3"
  ).
  enablePlugins(BuildInfoPlugin)

lazy val bindingsSlick =
  project.in(file("bindings-slick")).settings(
    name := "isomorphic-play-slick",
    libraryDependencies += scalaTestPlay
  ).
  settings(publishSettings("isomorphicPlaySlick")).
  enablePlugins(play.sbt.PlayScala).
  dependsOn(rootSlick).
  aggregate(rootSlick)

val scalaTestPlay =
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test" withSources ()

def baseSettings(projectName: String) = Seq(
  name := projectName,
  organization in ThisBuild := "io.github.godenji",
  sonatypeProfileName in ThisBuild := organization.value,
  version in ThisBuild := appVersion,
  crossScalaVersions in ThisBuild := scalaCrossVersions,
  scalaVersion in ThisBuild := scalaRelease,
  credentials ++= {
    val creds = Path.userHome / ".sonatype" / organization.value
    if (creds.exists) Seq(Credentials(creds)) else Nil
  },
  libraryDependencies +=
    "org.scala-lang" % "scala-reflect" % scalaVersion.value
)

def publishSettings(projectName: String) = Seq(
  pomExtra := pomDetail,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishArtifact in (Compile, packageDoc) := true,
  publishArtifact in (Compile, packageSrc) := true,
  pomIncludeRepository := { _ => false },
  buildInfoKeys := Seq[BuildInfoKey](version),
  buildInfoPackage := projectName,
  publishTo := getPublishToRepo.value
)

def getPublishToRepo = Def.setting {
  if (isSnapshot.value)
    Some(Opts.resolver.sonatypeSnapshots)
  else
    Some(Opts.resolver.sonatypeStaging)
}

def pomDetail =
  <inceptionYear>2015</inceptionYear>
  <url>https://github.com/godenji/isomorphic</url>
  <licenses>
    <license>
      <name>Two-clause BSD-style license</name>
      <url>https://github.com/godenji/isomorphic/blob/master/LICENSE.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:godenji/isomorphic.git</url>
    <connection>scm:git:git@github.com:godenji/isomorphic</connection>
  </scm>
  <developers>
    <developer>
      <id>godenji</id>
      <name>N.S. Cutler</name>
      <url>https://github.com/godenji</url>
    </developer>
  </developers>
