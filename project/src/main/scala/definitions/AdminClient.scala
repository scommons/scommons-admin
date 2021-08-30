package definitions

import com.typesafe.sbt.web.SbtWeb
import org.scalajs.jsenv.nodejs.NodeJSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._
import scommons.sbtplugin.project.CommonClientModule
import scoverage.ScoverageKeys._
import webscalajs.ScalaJSWeb

object AdminClient extends AdminModule with CommonClientModule {

  override val id: String = "scommons-admin-client"

  override val base: File = file("client")

  override def definition: Project = {
    super.definition
      .enablePlugins(ScalaJSWeb, SbtWeb)
      .settings(
        coverageExcludedPackages := coverageExcludedPackages.value +
          ";.*AdminMain" +
          ";.*AdminActions",

        // required for node.js >= v12.12.0
        // see:
        //   https://github.com/nodejs/node/pull/29919
        scalaJSLinkerConfig in Test ~= {
          _.withSourceMap(true)
        },
        jsEnv in Test := new NodeJSEnv(NodeJSEnv.Config().withArgs(List("--enable-source-maps")))
      )
  }

  override def internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    AdminClientApi.js
  )

  override def runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.runtimeDependencies.value ++ Seq(
      // specify your custom runtime dependencies here
    )
  }

  override def testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.testDependencies.value ++ Seq[ModuleID](
      // specify your custom test dependencies here
    ).map(_ % "test")
  }
}
