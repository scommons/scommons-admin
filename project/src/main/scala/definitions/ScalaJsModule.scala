package definitions

import org.scalajs.jsenv.nodejs.NodeJSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys.{coverageEnabled, coverageScalacPluginVersion}

object ScalaJsModule {

  val settings: Seq[Setting[_]] = Seq(
    // required for node.js >= v12.12.0
    // see:
    //   https://github.com/nodejs/node/pull/29919
    jsEnv in Test := new NodeJSEnv(NodeJSEnv.Config().withArgs(List("--enable-source-maps"))),
    scalaJSLinkerConfig in Test ~= {
      _.withSourceMap(true)
    },

    //TODO: remove these temporal fixes for Scala.js 1.1+ and scoverage
    coverageScalacPluginVersion := {
      val current = coverageScalacPluginVersion.value
      if (scalaJSVersion.startsWith("0.6")) current
      else "1.4.2" //the only version that supports Scala.js 1.1+
    },
    libraryDependencies ~= { modules =>
      if (scalaJSVersion.startsWith("0.6")) modules
      else modules.filter(_.organization != "org.scoverage")
    },
    libraryDependencies ++= {
      if (coverageEnabled.value) {
        if (scalaJSVersion.startsWith("0.6")) Nil
        else Seq(
          "org.scoverage" %% "scalac-scoverage-runtime_sjs1" % coverageScalacPluginVersion.value,
          "org.scoverage" %% "scalac-scoverage-plugin" % coverageScalacPluginVersion.value % "scoveragePlugin"
        )
      }
      else Nil
    }
  )
}
