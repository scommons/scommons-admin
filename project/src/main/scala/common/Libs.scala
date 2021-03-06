package common

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._
import scommons.sbtplugin.project.CommonLibs

object Libs extends CommonLibs {

  val scommonsNodejsVersion = "1.0.0-SNAPSHOT"
  val scommonsApiVersion = "1.0.0-SNAPSHOT"
  val scommonsServiceVersion = "1.0.0-SNAPSHOT"
  val scommonsReactVersion = "1.0.0-SNAPSHOT"
  val scommonsClientVersion = "1.0.0-SNAPSHOT"

  //////////////////////////////////////////////////////////////////////////////
  // shared dependencies

  lazy val scommonsApiCore = Def.setting("org.scommons.api" %%% "scommons-api-core" % scommonsApiVersion)
  lazy val scommonsApiAdmin = Def.setting("org.scommons.api" %%% "scommons-api-admin" % scommonsApiVersion)
  lazy val scommonsApiJodaTime = Def.setting("org.scommons.api" %%% "scommons-api-joda-time" % scommonsApiVersion)

  //////////////////////////////////////////////////////////////////////////////
  // jvm dependencies

  lazy val scommonsApiPlayWs = Def.setting("org.scommons.api" %% "scommons-api-play-ws" % scommonsApiVersion)
  lazy val scommonsServiceDao = Def.setting("org.scommons.service" %% "scommons-service-dao" % scommonsServiceVersion)
  
  lazy val postgresJdbc = Def.setting("org.postgresql" % "postgresql" % "42.2.5")
  lazy val playLiquibase = Def.setting("com.ticketfly" %% "play-liquibase" % "1.4")
  lazy val quillAsyncPostgres = Def.setting("io.getquill" %% "quill-async-postgres" % "1.4.0" /*exclude("io.netty", "netty-all")*/)

  //////////////////////////////////////////////////////////////////////////////
  // js dependencies

}
