package common

import common.Libs._
import sbt._
import scommons.sbtplugin.project.CommonTestLibs

object TestLibs extends CommonTestLibs {

  lazy val scommonsApiPlayWs = Def.setting("org.scommons.api" %% "scommons-api-play-ws" % scommonsApiVersion)

  lazy val dockerTestkitScalatest = Def.setting("com.whisk" %% "docker-testkit-scalatest" % "0.9.3")
  lazy val dockerTestkitImpl = Def.setting("com.whisk" %% "docker-testkit-impl-spotify" % "0.9.3")

  // Scala.js dependencies
}
