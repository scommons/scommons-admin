package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.BaseStateController
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.ui.{Buttons, UiComponent}
import scommons.client.util.{ActionsData, BrowsePath}

class UserController(apiActions: UserActions)
  extends BaseStateController[AdminStateDef, UserPanelProps] {

  lazy val uiComponent: UiComponent[UserPanelProps] = UserPanel

  def mapStateToProps(dispatch: Dispatch, state: AdminStateDef, props: Props[Unit]): UserPanelProps = {
    UserPanelProps(dispatch, apiActions, state.userState)
  }

  private lazy val usersItem = BrowseTreeItemData(
    "Users",
    BrowsePath("/"),
    Some(AdminImagesCss.group),
    ActionsData(Set(Buttons.REFRESH.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.userListFetch(dispatch, None, None))
    }),
    Some(apply())
  )

  def getUsersItem(path: BrowsePath): BrowseTreeItemData = usersItem.copy(
    path = path
  )
}
