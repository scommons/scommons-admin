package definitions

import common.{Libs, TestLibs}
import sbt._
import scommons.sbtplugin.project.CommonPlayModule
import webscalajs.WebScalaJS.autoImport._

object AdminServer extends AdminModule with CommonPlayModule {

  override val id: String = "scommons-admin-server"

  override def definition: Project = {
    super.definition
      .settings(
        scalaJSProjects := Seq(AdminClient.definition)
      )
  }

  override def internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    AdminClientApi.jvm
  )

  override def runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.runtimeDependencies.value ++ Seq(
      Libs.swaggerPlay.value,
      Libs.swaggerAnnotations.value,
      Libs.swaggerUi.value
    )
  }

  override def testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.testDependencies.value ++ Seq[ModuleID](
      TestLibs.scommonsApiPlayWs.value
    ).map(_ % "it,test")
  }
}
