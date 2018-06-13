package definitions

import com.typesafe.sbt.digest.Import.digest
import com.typesafe.sbt.gzip.Import.gzip
import com.typesafe.sbt.web.SbtWeb.autoImport._
import common.{Libs, TestLibs}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import play.sbt.routes.RoutesKeys
import play.sbt.{PlayImport, PlayLayoutPlugin, PlayScala}
import sbt._
import scommons.sbtplugin.WebpackAssetsPlugin.autoImport._
import scoverage.ScoverageKeys.coverageExcludedPackages
import webscalajs.WebScalaJS.autoImport._

import scalajsbundler.sbtplugin.WebScalaJSBundlerPlugin

object AdminServer extends BasicModule {

  override val id: String = "scommons-admin-server"

  override def base: File = file(id)

  override def definition: Project = {
    super.definition
      .enablePlugins(PlayScala, WebScalaJSBundlerPlugin)
      .disablePlugins(PlayLayoutPlugin)
      .configs(IntegrationTest)
      .settings(Defaults.itSettings: _*)
      .settings(
        RoutesKeys.routesImport -= "controllers.Assets.Asset", //remove unused import warning from routes file
        coverageExcludedPackages := "<empty>;Reverse.*;router.*",

        scalaJSProjects := Seq(AdminClient.definition),
        pipelineStages in Assets := Seq(scalaJSPipeline),
        pipelineStages := Seq(digest, gzip),

        // Expose as sbt-web assets some webpack build files of the `client` project
        //npmAssets ++= WebpackAssets.ofProject(ShowcaseClient.definition) { build => (build / "styles").*** }.value
        webpackAssets in fastOptJS ++= WebpackAssets.ofProject(fastOptJS, AdminClient.definition) { build => (build / "styles").*** }.value,
        webpackAssets in fullOptJS ++= WebpackAssets.ofProject(fullOptJS, AdminClient.definition) { build => (build / "styles").*** }.value
      )
  }

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    AdminClientApi.jvm
  )

  override val superRepoProjectsDependencies: Seq[(String, String, Option[String])] = Seq(
    ("scommons-service", "scommons-service-play", None)
  )

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    Libs.scommonsServicePlay.value,
    Libs.scommonsClientAssets.value,

    PlayImport.guice,
    Libs.play.value,
    Libs.scaldiPlay.value,
    Libs.slf4jApi.value,
    Libs.logback.value,
    Libs.swaggerPlay.value,
    Libs.swaggerAnnotations.value,
    Libs.swaggerUi.value
  ))

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    TestLibs.scommonsApiPlayWs.value,
    TestLibs.scalaTestPlusPlay.value,
    TestLibs.akkaStreamTestKit.value,
    TestLibs.mockito.value
  ).map(_ % "it,test"))
}
