package definitions

import common.{Common, Libs}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import sbt.Keys._
import sbt._

object AdminClientApi {

  val id: String = "scommons-admin-client-api"

  def base: File = file(id)

  private lazy val `scommons-admin-client-api`: CrossProject = crossProject.crossType(CrossType.Pure).in(base)
    .settings(Common.settings: _*)
    .settings(
      libraryDependencies ++= Seq(
        Libs.scommonsApiCore.value,
        Libs.scommonsApiJodaTime.value
      )
    ).jvmSettings(
      // Add JVM-specific settings here
    ).jsSettings(
      // Add JS-specific settings here
    )

  lazy val jvm: Project = `scommons-admin-client-api`.jvm

  lazy val js: Project = `scommons-admin-client-api`.js
}
