logLevel := Level.Warn

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.2" withSources)
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.1.1" withSources)
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.1")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.4")
