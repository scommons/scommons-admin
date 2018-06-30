package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.ReactDOM
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.ReactRedux._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.redux.{ReactRedux, Redux}
import io.github.shogowada.scalajs.reactjs.router.WithRouter
import io.github.shogowada.scalajs.reactjs.router.dom.RouterDOM._
import org.scalajs.dom
import scommons.client.app._
import scommons.client.task._
import scommons.client.ui.Buttons

import scala.scalajs.js.annotation.JSExportTopLevel

object AdminMain {

  @JSExportTopLevel("scommons.admin.client.main")
  def main(args: Array[String]): Unit = {
    val mountNode = dom.document.getElementById("root")

    dom.document.title = "scommons-admin"

    val store = Redux.createStore(AdminStateReducer.reduce)

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
            <(RouteController()).empty,
            <(TaskController()).empty
          )
        )
      ),
      mountNode
    )
  }
}

object RouteController {

  def apply(): ReactClass = reactClass

  private lazy val reactClass = ReactRedux.connectAdvanced(
    (dispatch: Dispatch) => {

      (state: AdminState, _: Props[Unit]) => {
        AppBrowseControllerProps(
          List(Buttons.REFRESH, Buttons.ADD, Buttons.REMOVE, Buttons.EDIT),
          AdminStateReducer.getTreeRoots(state),
          dispatch
        )
      }
    }
  )(AppBrowseController())
}

object TaskController {

  def apply(): ReactClass = reactClass

  private lazy val reactClass = ReactRedux.connectAdvanced(
    (_: Dispatch) => {

      (state: AdminState, _: Props[Unit]) => {
        TaskManagerProps(state.currentTask)
      }
    }
  )(TaskManager())
}
