package common

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Libs {

  val scommonsApiVersion = "0.1.0-SNAPSHOT"
  val scommonsServiceVersion = "0.1.0-SNAPSHOT"
  val scommonsClientVersion = "0.1.0-SNAPSHOT"

  val akkaVersion = "2.5.6"
  private val playVer = "2.6.7"

  //////////////////////////////////////////////////////////////////////////////
  // shared dependencies

  lazy val scommonsApiCore = Def.setting("org.scommons.api" %%% "scommons-api-core" % scommonsApiVersion)
  lazy val scommonsApiJodaTime = Def.setting("org.scommons.api" %%% "scommons-api-joda-time" % scommonsApiVersion)

  //////////////////////////////////////////////////////////////////////////////
  // jvm dependencies

  lazy val scommonsServicePlay = Def.setting("org.scommons.service" %% "scommons-service-play" % scommonsServiceVersion)
  lazy val scommonsClientAssets = Def.setting("org.scommons.client" %% "scommons-client-assets" % scommonsClientVersion)

  lazy val play = Def.setting("com.typesafe.play" %% "play" % playVer)
  lazy val scaldiPlay = Def.setting("org.scaldi" %% "scaldi-play" % "0.5.17")

  lazy val logback = Def.setting("ch.qos.logback" % "logback-classic" % "1.1.7")
  lazy val slf4jApi = Def.setting("org.slf4j" % "slf4j-api" % "1.7.12")
  lazy val log4jToSlf4j = Def.setting("org.apache.logging.log4j" % "log4j-to-slf4j" % "2.2")
  lazy val jclOverSlf4j = Def.setting("org.slf4j" % "jcl-over-slf4j" % "1.7.12")

  lazy val swaggerPlay = Def.setting("io.swagger" %% "swagger-play2" % "1.6.0")
  lazy val swaggerAnnotations = Def.setting("io.swagger" % "swagger-annotations" % "1.5.16")
  lazy val swaggerUi = Def.setting("org.webjars" % "swagger-ui" % "2.2.2")

  //////////////////////////////////////////////////////////////////////////////
  // js dependencies

  lazy val scommonsClientUi = Def.setting("org.scommons.client" %%% "scommons-client-ui" % scommonsClientVersion)
}
