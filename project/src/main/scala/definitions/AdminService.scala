package definitions

import common.{Libs, TestLibs}
import sbt.Keys._
import sbt._

object AdminService extends AdminModule {

  override val id: String = "scommons-admin-service"

  override val base: File = file("service")

  override def definition: Project = super.definition
    .settings(
      description := "Common Admin Service utilities"
    )

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    AdminServiceApi.definition
  )

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    Libs.scommonsApiPlayWs.value
  ))

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    TestLibs.scalaTest.value,
    TestLibs.mockito.value
  ).map(_ % "test"))
}
