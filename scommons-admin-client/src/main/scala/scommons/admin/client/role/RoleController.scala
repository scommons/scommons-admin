package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.role.RoleActions._
import scommons.admin.client.role.permission.RolePermissionController
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{BaseStateAndRouteController, RouteParams}
import scommons.client.ui.tree.{BrowseTreeItemData, BrowseTreeNodeData}
import scommons.client.ui.Buttons
import scommons.client.util.{ActionsData, BrowsePath}
import scommons.react.UiComponent

class RoleController(apiActions: RoleActions)
  extends BaseStateAndRouteController[AdminStateDef, RolePanelProps] {

  lazy val uiComponent: UiComponent[RolePanelProps] = RolePanel

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              routeParams: RouteParams): RolePanelProps = {

    val pathParams = routeParams.pathParams

    RolePanelProps(dispatch, apiActions, state.roleState,
      extractSystemId(pathParams), extractSystemRoleId(pathParams))
  }

  private lazy val rolesNode = BrowseTreeNodeData(
    "Roles",
    BrowsePath("/"),
    Some(AdminImagesCss.role),
    ActionsData(Set(Buttons.REFRESH.command, Buttons.ADD.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.roleListFetch(dispatch))
      case Buttons.ADD.command => dispatch(RoleCreateRequestAction(create = true))
    }),
    None
  )

  private lazy val roleItem = BrowseTreeItemData(
    "",
    BrowsePath("/"),
    Some(AdminImagesCss.role),
    ActionsData(Set(Buttons.EDIT.command), dispatch => {
      case Buttons.EDIT.command => dispatch(RoleUpdateRequestAction(update = true))
    }),
    None
  )

  def getRolesNode(path: BrowsePath): BrowseTreeNodeData = rolesNode.copy(
    path = path
  )

  def getRoleItem(path: BrowsePath,
                  data: RoleData,
                  rolePermissionController: RolePermissionController): BrowseTreeItemData = {
    
    roleItem.copy(
      text = data.title,
      path = BrowsePath(s"$path/${data.id.get}"),
      reactClass = Some(rolePermissionController())
    )
  }
}
