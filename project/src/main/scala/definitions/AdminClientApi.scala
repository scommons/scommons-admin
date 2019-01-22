package definitions

import common.Libs
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import sbt.Keys._
import sbt._
import scommons.sbtplugin.project.CommonModule
import scoverage.ScoverageKeys._

object AdminClientApi {

  val id: String = "scommons-admin-client-api"

  val base: File = file("client-api")
  
  private lazy val `scommons-admin-client-api`: CrossProject = crossProject
    .crossType(CrossType.Pure).in(base)
    .settings(CommonModule.settings: _*)
    .settings(AdminModule.settings: _*)
    .settings(
      libraryDependencies ++= Seq(
        Libs.scommonsApiCore.value,
        Libs.scommonsApiAdmin.value,
        Libs.scommonsApiJodaTime.value
      )
    ).jvmSettings(
      // Add JVM-specific settings here
    ).jsSettings(
      // Add JS-specific settings here
      coverageEnabled := false
    )

  lazy val jvm: Project = `scommons-admin-client-api`.jvm

  lazy val js: Project = `scommons-admin-client-api`.js
}
