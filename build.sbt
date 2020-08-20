import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val scalaRelease = "2.13.3"
val scalaCrossVersions = Seq("2.12.12", scalaRelease)

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
  settings(publishSettings("isomorphic")).
  settings(
    name := "isomorphic",
    organization in ThisBuild := "io.github.godenji",
    sonatypeProfileName in ThisBuild := organization.value,
    version in ThisBuild := "0.1.7",
    crossScalaVersions in ThisBuild := scalaCrossVersions,
    scalaVersion in ThisBuild := scalaRelease,
    credentials ++= {
      val creds = Path.userHome / ".sonatype" / organization.value
      if (creds.exists) Seq(Credentials(creds)) else Nil
    },
    libraryDependencies +=
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
  ).
  enablePlugins(BuildInfoPlugin)

lazy val bindings =
  project.in(file("bindings")).settings(
    name := "isomorphic-play",
    libraryDependencies +=
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % "test" withSources ()
  ).
  settings(publishSettings("isomorphicPlay")).
  enablePlugins(play.sbt.PlayScala).
  dependsOn(sharedJvm, sharedJs).
  aggregate(sharedJvm, sharedJs)

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
