package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.role.RoleActions._
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.BaseStateAndRouteController
import scommons.client.ui.tree.{BrowseTreeItemData, BrowseTreeNodeData}
import scommons.client.ui.{Buttons, UiComponent}
import scommons.client.util.{ActionsData, BrowsePath}

class RoleController(apiActions: RoleActions)
  extends BaseStateAndRouteController[AdminStateDef, RolePanelProps] {

  lazy val uiComponent: UiComponent[RolePanelProps] = RolePanel

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              props: Props[Unit],
                              routerProps: RouterProps): RolePanelProps = {

    val path = routerProps.location.pathname

    RolePanelProps(dispatch, apiActions, state.roleState,
      extractSystemId(path), extractRoleId(path))
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

  def getRolesNode(rolesPath: String): BrowseTreeNodeData = rolesNode.copy(
    path = BrowsePath(rolesPath)
  )

  def getRoleItem(rolesPath: String, data: RoleData): BrowseTreeItemData = roleItem.copy(
    text = data.title,
    path = BrowsePath(s"$rolesPath/${data.id.get}")
  )
}
