package definitions

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.Cmd
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.universal.UniversalDeployPlugin
import common.Libs
import common.CommonPlayModule
import sbt.Keys._
import sbt._
import webscalajs.WebScalaJS.autoImport._

object AdminServer extends AdminModule with CommonPlayModule {

  val port = Seq(9000)

  override val id: String = "scommons-admin-server"

  override val base: File = file("server")

  override def definition: Project = {
    super.definition
      .enablePlugins(
        JavaAppPackaging,
        UniversalDeployPlugin
      )
      .settings(
        scalaJSProjects := Seq(AdminClient.definition),

        mainClass in Compile := Some("play.core.server.ProdServerStart"),
        dockerExposedPorts := port,

        //dockerImageCreationTask := (publishLocal in Docker).value,
        dockerBaseImage := "openjdk:9-slim",
        mappings in (Compile, packageDoc) := Seq(),

        dockerCommands := (dockerCommands.value match {
          case Seq(from@Cmd("FROM", _), rest@_*) =>
            Seq(
              from,
              //set JVM TTL, see https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-jvm-ttl.html
              Cmd("RUN", "mkdir", "-p", "$JAVA_HOME/jre/lib/security"),
              Cmd("RUN", "echo", "networkaddress.cache.ttl=60", ">>", "$JAVA_HOME/jre/lib/security/java.security")
            ) ++ rest
        })
      )
  }

  override def internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    AdminClientApi.jvm,
    AdminService.definition,
    AdminDao.definition
  )

  override def runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.runtimeDependencies.value ++ Seq(
      Libs.postgresJdbc.value,
      Libs.playLiquibase.value,
      
      Libs.swaggerPlay.value,
      Libs.swaggerAnnotations.value,
      Libs.swaggerUi.value
    )
  }

  override def testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.testDependencies.value ++ Seq[ModuleID](
      // your dependencies here
    ).map(_ % "it,test")
  }
}
