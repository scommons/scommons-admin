package common

import common.Libs._
import sbt._
import scommons.sbtplugin.project.CommonTestLibs

object TestLibs extends CommonTestLibs {

  lazy val scommonsApiPlayWs = Def.setting("org.scommons.api" %% "scommons-api-play-ws" % scommonsApiVersion)

  // Scala.js dependencies
}
