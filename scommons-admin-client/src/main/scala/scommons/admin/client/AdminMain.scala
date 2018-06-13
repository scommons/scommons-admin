package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.ReactDOM
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExportTopLevel

object AdminMain {

  @JSExportTopLevel("scommons.admin.client.main")
  def main(args: Array[String]): Unit = {
    val mountNode = dom.document.getElementById("root")

    dom.document.title = "scommons-admin"

//    val store = Redux.createStore(AdminReducer.reduce)
//
//    val appMainPanelProps = AppMainPanelProps(
//      name = "scommons-admin",
//      user = "me",
//      copyright = "© scommons-admin",
//      version = "(version: 0.1.0-SNAPSHOT)"
//    )

    ReactDOM.render(
//      <.Provider(^.store := store)(
//        <.HashRouter()(
//          <(WithRouter(AppMainPanel()))(^.wrapped := appMainPanelProps)(
//            <(RouteController()).empty,
//            <(TaskController()).empty
//          )
//        )
//      ),
      <.div(^.className := AdminImagesCss.computer)(),
      mountNode
    )
  }
}
