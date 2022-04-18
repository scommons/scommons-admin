package definitions

import common.Libs
import sbt.Keys._
import sbt._
import sbtcrossproject.CrossPlugin.autoImport._
import sbtcrossproject.{CrossProject, JVMPlatform}
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import scommons.sbtplugin.project.CommonModule
import scoverage.ScoverageKeys._

object AdminClientApi {

  val id: String = "scommons-admin-client-api"

  val base: File = file("client-api")
  
  private lazy val `scommons-admin-client-api`: CrossProject = CrossProject(id, base)(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
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
      Seq(
        coverageEnabled := false
      ) ++ ScalaJsModule.settings: _*
    )

  lazy val jvm: Project = `scommons-admin-client-api`.jvm
  lazy val js: Project = `scommons-admin-client-api`.js
}
