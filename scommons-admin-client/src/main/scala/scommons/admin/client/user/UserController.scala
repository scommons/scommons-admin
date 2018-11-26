package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions.UserParamsChangedAction
import scommons.admin.client.user.system.UserSystemActions
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{BaseStateAndRouteController, RouteParams}
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.ui.{Buttons, UiComponent}
import scommons.client.util.{ActionsData, BrowsePath}

class UserController(companyActions: CompanyActions,
                     userActions: UserActions,
                     userSystemActions: UserSystemActions
                    ) extends BaseStateAndRouteController[AdminStateDef, UserPanelProps] {

  lazy val uiComponent: UiComponent[UserPanelProps] = UserPanel

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              routeParams: RouteParams): UserPanelProps = {

    val pathParams = routeParams.pathParams
    val params = UserParams(extractUserId(pathParams), extractUserTab(pathParams))
    
    UserPanelProps(dispatch, companyActions, userActions, userSystemActions, state.userState, state.userSystemState,
      params,
      onChangeParams = { params =>
        val path = buildUsersPath(params)
        if (pathParams.path != path.value) {
          routeParams.push(path.value)
        }
        dispatch(UserParamsChangedAction(params))
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
