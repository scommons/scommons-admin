package definitions

import org.scalajs.jsenv.nodejs.NodeJSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object ScalaJsModule {

  val settings: Seq[Setting[_]] = Seq(
    // required for node.js >= v12.12.0
    // see:
    //   https://github.com/nodejs/node/pull/29919
    Test / jsEnv := new NodeJSEnv(NodeJSEnv.Config().withArgs(List("--enable-source-maps"))),
    Test / scalaJSLinkerConfig ~= {
      _.withSourceMap(true)
    }
  )
}
