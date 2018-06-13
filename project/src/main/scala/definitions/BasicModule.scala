package definitions

import common.Common
import sbt.Keys._
import sbt._
import scommons.sbtplugin.mecha.MechaProjectBuild

trait BasicModule extends ProjectDef
  with MechaProjectBuild {

  override val repoName = "scommons-admin"

  override def definition: Project = Project(id = id, base = base)
    .dependsOn(internalDependencies: _*)
    .settings(Common.settings: _*)
    .settings(
      libraryDependencies ++= (runtimeDependencies.value ++ testDependencies.value)
    )
    .settings(Seq(
      libraryDependencies --= excludeSuperRepoDependencies.value
    ))
    .dependsOnSuperRepo

  override def superRepoProjectsDependencies: Seq[(String, String, Option[String])] = Nil
}
