package scommons.admin.client.role.permission

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.AdminStateDef
import scommons.client.controller.{BaseStateAndRouteController, RouteParams}
import scommons.react.UiComponent

class RolePermissionController(apiActions: RolePermissionActions)
  extends BaseStateAndRouteController[AdminStateDef, RolePermissionPanelProps] {

  lazy val uiComponent: UiComponent[RolePermissionPanelProps] = RolePermissionPanel

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              routeParams: RouteParams): RolePermissionPanelProps = {

    val pathParams = routeParams.pathParams

    RolePermissionPanelProps(dispatch, apiActions, state.rolePermissionState,
      extractSystemRoleId(pathParams).getOrElse(-1))
  }
}
