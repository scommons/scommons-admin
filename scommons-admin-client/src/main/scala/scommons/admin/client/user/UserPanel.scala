package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.user._
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions._
import scommons.client.ui._
import scommons.client.util.ActionsData

case class UserPanelProps(dispatch: Dispatch,
                          companyActions: CompanyActions,
                          userActions: UserActions,
                          data: UserState,
                          selectedParams: UserParams,
                          onChangeParams: UserParams => Unit)

object UserPanel extends UiComponent[UserPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped

      if (props.selectedParams != props.data.params) {
        props.onChangeParams(props.selectedParams)
      }
    },
    componentDidUpdate = { (self, prevProps, _) =>
      val props = self.props.wrapped
      if (props.selectedParams != prevProps.wrapped.selectedParams) {
        if (props.selectedParams != props.data.params) {
          props.onChangeParams(props.selectedParams)
        }
      }
    },
    render = { self =>
      val props = self.props.wrapped
      val userDetailsData = props.data.userDetails.filter(d => props.selectedParams.userId == d.user.id)
  
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
          dispatch = props.dispatch,
          actions = props.userActions,
          data = props.data,
          selectedUserId = props.selectedParams.userId,
          onChangeSelect = { userId =>
            props.onChangeParams(props.selectedParams.copy(userId = userId))
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
