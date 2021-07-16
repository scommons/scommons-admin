//resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin(("org.scommons.sbt" % "sbt-scommons-plugin" % "0.5.0-SNAPSHOT").changing())

addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler-sjs06" % "0.16.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.3")
