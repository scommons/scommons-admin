package common

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._
import scommons.sbtplugin.project.CommonLibs

object Libs extends CommonLibs {

  val scommonsNodejsVersion = "0.8.2"
  val scommonsApiVersion = "0.8.1"
  val scommonsServiceVersion = "0.8.0"
  val scommonsReactVersion = "0.8.0"
  val scommonsClientVersion = "0.8.0"

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
  lazy val liquibaseCore = Def.setting("org.liquibase" % "liquibase-core" % "3.6.2")
  lazy val liquibaseSlf4j = Def.setting("com.mattbertolini" % "liquibase-slf4j" % "2.0.0")
  lazy val quillAsyncPostgres = Def.setting("io.getquill" %% "quill-async-postgres" % "3.7.2" /*exclude("io.netty", "netty-all")*/)

  //////////////////////////////////////////////////////////////////////////////
  // js dependencies

}
