package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{BaseStateAndRouteController, RouteParams}
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.ui.{Buttons, UiComponent}
import scommons.client.util.{ActionsData, BrowsePath}

class SystemUserController(apiActions: SystemUserActions)
  extends BaseStateAndRouteController[AdminStateDef, SystemUserPanelProps] {

  lazy val uiComponent: UiComponent[SystemUserPanelProps] = SystemUserPanel

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              routeParams: RouteParams): SystemUserPanelProps = {

    val pathParams = routeParams.pathParams

    SystemUserPanelProps(dispatch, apiActions, state.systemUserState,
      extractSystemId(pathParams))
  }

  private lazy val usersItem = BrowseTreeItemData(
    "Users",
    BrowsePath("/"),
    Some(AdminImagesCss.group),
    ActionsData.empty,
    Some(apply())
  )

  def getUsersItem(path: BrowsePath, systemId: Int): BrowseTreeItemData = {
    usersItem.copy(
      path = path,
      actions = ActionsData(Set(Buttons.REFRESH.command), dispatch => {
        case Buttons.REFRESH.command => dispatch(apiActions.systemUserListFetch(dispatch, systemId, None, None))
      })
    )
  }
}
