package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.user._
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions._
import scommons.admin.client.user.system._
import scommons.client.ui._
import scommons.client.util.ActionsData
import scommons.react._

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
        <(ButtonsPanel())(^.wrapped := ButtonsPanelProps(
          List(Buttons.ADD, Buttons.EDIT),
          ActionsData(Set(Buttons.ADD.command) ++ userDetailsData.map(_ => Buttons.EDIT.command), dispatch => {
            case Buttons.ADD.command => dispatch(UserCreateRequestAction(create = true))
            case Buttons.EDIT.command => dispatch(UserUpdateRequestAction(update = true))
          }),
          props.dispatch
        ))(),
        
        <(UserTablePanel())(^.wrapped := UserTablePanelProps(
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
  
        <(UserEditPopup())(^.wrapped := UserEditPopupProps(
          dispatch = props.dispatch,
          actions = props.companyActions,
          show = props.data.showCreatePopup,
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
        ))(),
        
        userDetailsData.toList.flatMap { data =>
          List(
            <(UserDetailsPanel())(^.wrapped := UserDetailsPanelProps(
              renderSystems = { _ =>
                <(UserSystemPanel())(^.wrapped := UserSystemPanelProps(
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
            ))(),
  
            <(UserEditPopup())(^.wrapped := UserEditPopupProps(
              dispatch = props.dispatch,
              actions = props.companyActions,
              show = props.data.showEditPopup,
              title = "Edit User",
              initialData = data,
              onSave = { updatedData =>
                props.dispatch(props.userActions.userUpdate(props.dispatch, updatedData))
              },
              onCancel = { () =>
                props.dispatch(UserUpdateRequestAction(update = false))
              }
            ))()
          )
        }
      )
    }
  )
}
