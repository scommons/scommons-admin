package definitions

import com.typesafe.sbt.web.SbtWeb
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
      .settings(ScalaJsModule.settings: _*)
      .settings(
        coverageExcludedPackages := coverageExcludedPackages.value +
          ";.*AdminMain" +
          ";.*AdminActions"
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
