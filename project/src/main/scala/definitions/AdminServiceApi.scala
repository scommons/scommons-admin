package definitions

import common.{Libs, TestLibs}
import sbt.Keys._
import sbt._

object AdminServiceApi extends AdminModule {

  override val id: String = "scommons-admin-service-api"

  override val base: File = file("service-api")

  override def definition: Project = super.definition
    .settings(
      description := "Common Admin Service REST API"
    )

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Nil

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    Libs.scommonsApiCore.value,
    Libs.scommonsApiAdmin.value,
    Libs.scommonsApiJodaTime.value
  ))

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    TestLibs.scalaTest.value,
    TestLibs.mockito.value
  ).map(_ % "test"))
}
