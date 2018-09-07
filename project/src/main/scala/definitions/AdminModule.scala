package definitions

import common.Libs
import sbt.Keys._
import sbt._
import scommons.sbtplugin.project.CommonModule

trait AdminModule extends CommonModule {

  override val repoName = "scommons-admin"

  val scommonsServiceVersion: String = Libs.scommonsServiceVersion
  val scommonsClientVersion: String = Libs.scommonsClientVersion
  val scommonsApiVersion: String = Libs.scommonsApiVersion
  
  override def definition: Project = {
    super.definition
      .settings(AdminModule.settings: _*)
  }
}

object AdminModule {

  val settings: Seq[Setting[_]] = Seq(
    organization := "org.scommons.admin"
  )
}
