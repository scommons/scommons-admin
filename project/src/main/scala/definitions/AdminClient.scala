package definitions

import sbt._
import scommons.sbtplugin.project.CommonClientModule

object AdminClient extends AdminModule with CommonClientModule {

  override val id: String = "scommons-admin-client"

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
