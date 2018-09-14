package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.ReactDOM
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.redux.ReactRedux._
import io.github.shogowada.scalajs.reactjs.redux.Redux
import io.github.shogowada.scalajs.reactjs.router.WithRouter
import io.github.shogowada.scalajs.reactjs.router.dom.RouterDOM._
import org.scalajs.dom
import scommons.admin.client.company.CompanyController
import scommons.admin.client.role.RoleController
import scommons.admin.client.role.permission.RolePermissionController
import scommons.admin.client.system.SystemController
import scommons.admin.client.system.group.SystemGroupController
import scommons.admin.client.user.UserController
import scommons.client.app._

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

    val apiActions = AdminActions
    val companyController = new CompanyController(apiActions)
    val userController = new UserController(apiActions)
    val envController = new SystemGroupController(apiActions, apiActions)
    val appController = new SystemController(apiActions)
    val roleController = new RoleController(apiActions)
    val rolePermissionController = new RolePermissionController(apiActions)
    val routeController = new AdminRouteController(
      companyController,
      userController,
      envController,
      appController,
      roleController,
      rolePermissionController
    )

    ReactDOM.render(
      <.Provider(^.store := store)(
        <.HashRouter()(
          <(WithRouter(AppMainPanel()))(^.wrapped := appMainPanelProps)(
            <(routeController())()(
              <(WithRouter(appController())).empty,
              <(WithRouter(roleController())).empty
            ),
            <(AdminTaskController()).empty,
            <(WithRouter(envController())).empty
          )
        )
      ),
      mountNode
    )
  }
}
