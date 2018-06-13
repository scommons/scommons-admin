package definitions

import sbt._

trait ProjectDef {

  val id: String

  def base: File = file(id)

  def definition: Project

  def runtimeDependencies: Def.Initialize[Seq[ModuleID]]

  def testDependencies: Def.Initialize[Seq[ModuleID]]

  def internalDependencies: Seq[ClasspathDep[ProjectReference]]
}
