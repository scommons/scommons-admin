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
  lazy val liquibaseCore = Def.setting("org.liquibase" % "liquibase-core" % "3.6.2")
  lazy val liquibaseSlf4j = Def.setting("com.mattbertolini" % "liquibase-slf4j" % "2.0.0")
  lazy val quillAsyncPostgres = Def.setting("io.getquill" %% "quill-async-postgres" % "3.7.2" /*exclude("io.netty", "netty-all")*/)

  //////////////////////////////////////////////////////////////////////////////
  // js dependencies

}
