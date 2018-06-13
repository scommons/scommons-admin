package definitions

import com.typesafe.sbt.web.SbtWeb
import common.Libs
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys._
import webscalajs.ScalaJSWeb

import scalajsbundler.BundlingMode
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object AdminClient extends ScalaJsModule {

  override val id: String = "scommons-admin-client"

  override def base: File = file(id)

  override def definition: Project = {
    super.definition
      .enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb, SbtWeb)
      .settings(
        coverageEnabled := false,

        scalaJSUseMainModuleInitializer := true,
        webpackBundlingMode := BundlingMode.LibraryOnly(),

        //dev
        webpackConfigFile in fastOptJS := Some(baseDirectory.value / "admin.webpack.config.js"),
        //production
        webpackConfigFile in fullOptJS := Some(baseDirectory.value / "admin.webpack.config.js"),
        //reload workflow and tests
        webpackConfigFile in Test := Some(baseDirectory.value / "test.webpack.config.js")
      )
  }

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    AdminClientApi.js
  )

  override val superRepoProjectsDependencies: Seq[(String, String, Option[String])] = Seq(
    ("scommons-client", "scommons-client-ui", None)
  )

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    Libs.scommonsClientUi.value
  ))

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Nil)
}
