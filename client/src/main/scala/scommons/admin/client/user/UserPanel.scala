package scommons.admin.client.user

import scommons.admin.client.api.user._
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions._
import scommons.admin.client.user.system._
import scommons.client.ui._
import scommons.client.util.ActionsData
import scommons.react._
import scommons.react.redux.Dispatch

import scala.concurrent.ExecutionContext.Implicits.global

case class UserPanelProps(dispatch: Dispatch,
                          companyActions: CompanyActions,
                          userActions: UserActions,
                          userSystemActions: UserSystemActions,
                          data: UserState,
                          systemData: UserSystemState,
                          selectedParams: UserParams,
                          onChangeParams: UserParams => Unit)

object UserPanel extends ClassComponent[UserPanelProps] {

  private[user] var buttonsPanelComp: UiComponent[ButtonsPanelProps] = ButtonsPanel
  private[user] var userTablePanelComp: UiComponent[UserTablePanelProps] = UserTablePanel
  private[user] var userEditPopupComp: UiComponent[UserEditPopupProps] = UserEditPopup
  private[user] var userDetailsPanelComp: UiComponent[UserDetailsPanelProps] = UserDetailsPanel
  private[user] var userSystemPanelComp: UiComponent[UserSystemPanelProps] = UserSystemPanel

  protected def create(): ReactClass = createClass[Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.data.dataList.isEmpty) {
        props.dispatch(props.userActions.userListFetch(props.dispatch, None, None))
      }
      props.selectedParams.userId.foreach { userId =>
        if (!props.data.userDetails.flatMap(_.user.id).contains(userId)) {
          props.dispatch(props.userActions.userFetch(props.dispatch, userId))
        }
      }
      if (props.selectedParams != props.data.params) {
        props.onChangeParams(props.selectedParams)
      }
    },
    componentDidUpdate = { (self, prevProps, _) =>
      val props = self.props.wrapped
      if (props.selectedParams != prevProps.wrapped.selectedParams) {
        props.selectedParams.userId.foreach { userId =>
          if (!props.data.userDetails.flatMap(_.user.id).contains(userId)) {
            props.dispatch(props.userActions.userFetch(props.dispatch, userId))
          }
        }
        if (props.selectedParams != props.data.params) {
          props.onChangeParams(props.selectedParams)
        }
      }
    },
    render = { self =>
      val props = self.props.wrapped
      val userDetailsData = props.data.userDetails.filter(_ => props.selectedParams.userId.isDefined)
  
      <.div()(
        <(buttonsPanelComp())(^.wrapped := ButtonsPanelProps(
          List(Buttons.ADD, Buttons.EDIT),
          ActionsData(Set(Buttons.ADD.command) ++ userDetailsData.map(_ => Buttons.EDIT.command), dispatch => {
            case Buttons.ADD.command => dispatch(UserCreateRequestAction(create = true))
            case Buttons.EDIT.command => dispatch(UserUpdateRequestAction(update = true))
          }),
          props.dispatch
        ))(),
        
        <(userTablePanelComp())(^.wrapped := UserTablePanelProps(
          data = props.data,
          selectedUserId = props.selectedParams.userId,
          onChangeSelect = { userId =>
            val fetchAction = props.userActions.userFetch(props.dispatch, userId)
            fetchAction.task.future.map { _ =>
              props.onChangeParams(props.selectedParams.copy(userId = Some(userId)))
            }
            props.dispatch(fetchAction)
          },
          onLoadData = { (offset, symbols) =>
            props.onChangeParams(props.selectedParams.copy(userId = None))
            props.dispatch(props.userActions.userListFetch(props.dispatch, offset, symbols))
          }
        ))(),
        
        if (props.data.showCreatePopup) Some(
          <(userEditPopupComp())(^.wrapped := UserEditPopupProps(
            dispatch = props.dispatch,
            actions = props.companyActions,
            title = "New User",
            initialData = UserDetailsData(
              user = UserData(
                id = None,
                company = UserCompanyData(-1, ""),
                login = "",
                password = "",
                active = true
              ),
              profile = UserProfileData(
                email = "",
                firstName = "",
                lastName = "",
                phone = None
              )
            ),
            onSave = { data =>
              props.dispatch(props.userActions.userCreate(props.dispatch, data))
            },
            onCancel = { () =>
              props.dispatch(UserCreateRequestAction(create = false))
            }
          ))()
        ) else None,
        
        userDetailsData.toList.flatMap { data =>
          val res = List(
            <(userDetailsPanelComp())(^.wrapped := UserDetailsPanelProps(
              renderSystems = { _ =>
                <(userSystemPanelComp())(^.wrapped := UserSystemPanelProps(
                  dispatch = props.dispatch,
                  actions = props.userSystemActions,
                  systemData = props.systemData,
                  selectedUser = props.selectedParams.tab.getOrElse(UserDetailsTab.apps) match {
                    case UserDetailsTab.apps => Some(data.user)
                    case _ => None
                  }
                ))()
              },
              profile = data.profile,
              selectedTab = props.selectedParams.tab,
              onChangeTab = { tab =>
                props.onChangeParams(props.selectedParams.copy(tab = tab))
              }
            ))()
          )

          if (props.data.showEditPopup) {
            res :+ <(userEditPopupComp())(^.wrapped := UserEditPopupProps(
              dispatch = props.dispatch,
              actions = props.companyActions,
              title = "Edit User",
              initialData = data,
              onSave = { updatedData =>
                props.dispatch(props.userActions.userUpdate(props.dispatch, updatedData))
              },
              onCancel = { () =>
                props.dispatch(UserUpdateRequestAction(update = false))
              }
            ))()
          } else res
        }
      )
    }
  )
}
