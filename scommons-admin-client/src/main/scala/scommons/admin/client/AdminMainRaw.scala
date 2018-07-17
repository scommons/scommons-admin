package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.ReactDOM
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.redux.ReactRedux._
import io.github.shogowada.scalajs.reactjs.redux.Redux
import io.github.shogowada.scalajs.reactjs.router.WithRouter
import io.github.shogowada.scalajs.reactjs.router.dom.RouterDOM._
import org.scalajs.dom
import scommons.admin.client.action.ApiActions
import scommons.client.app._

import scala.scalajs.js.annotation.JSExportTopLevel

object AdminMainRaw {

  @JSExportTopLevel("scommons.admin.client.main")
  def main(args: Array[String]): Unit = {
    val mountNode = dom.document.getElementById("root")

    dom.document.title = "scommons-admin"

    val reducer = new AdminStateReducer(ApiActions)
    val store = Redux.createStore(reducer.reduce)

    val appMainPanelProps = AppMainPanelProps(
      name = "scommons-admin",
      user = "me",
      copyright = "© scommons-admin",
      version = "(version: 0.1.0-SNAPSHOT)"
    )

    ReactDOM.render(
      <.Provider(^.store := store)(
        <.HashRouter()(
          <(WithRouter(AppMainPanel()))(^.wrapped := appMainPanelProps)(
            <(AdminRouteController()).empty,
            <(AdminTaskController()).empty
          )
        )
      ),
      mountNode
    )
  }
}