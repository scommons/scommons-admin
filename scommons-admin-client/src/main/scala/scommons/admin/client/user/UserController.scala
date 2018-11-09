package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions.UsersPathChangedAction
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{BaseStateAndRouteController, RouteParams}
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.ui.{Buttons, UiComponent}
import scommons.client.util.{ActionsData, BrowsePath}

class UserController(companyActions: CompanyActions, userActions: UserActions)
  extends BaseStateAndRouteController[AdminStateDef, UserPanelProps] {

  lazy val uiComponent: UiComponent[UserPanelProps] = UserPanel

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              routeParams: RouteParams): UserPanelProps = {

    val pathParams = routeParams.pathParams
    
    UserPanelProps(dispatch, companyActions, userActions, state.userState, extractUserId(pathParams),
      onChangeSelect = { maybeUserId =>
        val path = buildUsersPath(maybeUserId)
        routeParams.push(path.value)
        dispatch(UsersPathChangedAction(path))
      }
    )
  }

  private lazy val usersItem = BrowseTreeItemData(
    "Users",
    BrowsePath("/"),
    Some(AdminImagesCss.group),
    ActionsData(Set(Buttons.REFRESH.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(userActions.userListFetch(dispatch, None, None))
    }),
    Some(apply())
  )

  def getUsersItem(path: BrowsePath): BrowseTreeItemData = usersItem.copy(
    path = path
  )
}
