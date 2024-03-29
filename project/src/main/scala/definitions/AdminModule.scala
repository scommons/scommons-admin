package definitions

import common.Libs
import org.scoverage.coveralls.Imports.CoverallsKeys._
import sbt.Keys._
import sbt._
import scommons.sbtplugin.project.CommonModule

trait AdminModule extends CommonModule {

  override val repoName = "scommons-admin"

  val scommonsServiceVersion: String = Libs.scommonsServiceVersion
  val scommonsNodejsVersion: String = Libs.scommonsNodejsVersion
  val scommonsReactVersion: String = Libs.scommonsReactVersion
  val scommonsClientVersion: String = Libs.scommonsClientVersion
  val scommonsApiVersion: String = Libs.scommonsApiVersion
  
  override def definition: Project = {
    super.definition
      .settings(AdminModule.settings: _*)
  }
}

object AdminModule {

  val settings: Seq[Setting[_]] = Seq(
    organization := "org.scommons.admin",

    coverallsService := GitHubActionsCI.jobId.map(_ => GitHubActionsCI)
  )
}
