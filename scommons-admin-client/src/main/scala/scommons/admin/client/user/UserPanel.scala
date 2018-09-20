package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions._
import scommons.client.ui._
import scommons.client.ui.tab.{TabItemData, TabPanel, TabPanelProps}
import scommons.client.util.ActionsData

case class UserPanelProps(dispatch: Dispatch,
                          actions: UserActions,
                          data: UserState)

object UserPanel extends UiComponent[UserPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped
    val selectedData = props.data.selected

    <.div()(
      <(ButtonsPanel())(^.wrapped := ButtonsPanelProps(
        List(Buttons.ADD, Buttons.EDIT),
        ActionsData(Set(Buttons.ADD.command) ++ selectedData.map(_ => Buttons.EDIT.command), dispatch => {
          case Buttons.ADD.command => dispatch(UserCreateRequestAction(create = true))
          case Buttons.EDIT.command => dispatch(UserUpdateRequestAction(update = true))
        }),
        props.dispatch
      ))(),
      
      <(UserTablePanel())(^.wrapped := UserTablePanelProps(props.dispatch, props.actions, props.data))(),

      <(UserEditPopup())(^.wrapped := UserEditPopupProps(
        show = props.data.showCreatePopup,
        title = "New User",
        onSave = { data =>
          props.dispatch(UserCreateRequestAction(create = false))
          props.dispatch(props.actions.userCreate(props.dispatch, data))
        },
        onCancel = { () =>
          props.dispatch(UserCreateRequestAction(create = false))
        },
        initialData = UserDetailsData(
          user = UserData(
            id = None,
            company = UserCompanyData(1, "Test Company"),
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
        )
      ))(),
      
      selectedData.toList.flatMap { data =>
        val tabItems = List(
          TabItemData("Profile", image = Some(AdminImagesCss.vcard), render = Some { _ =>
            <(UserProfilePanel())(^.wrapped := UserProfilePanelProps(data.profile))()
          })
        )
        
        List(
          <(TabPanel())(^.wrapped := TabPanelProps(tabItems))(),

          <(UserEditPopup())(^.wrapped := UserEditPopupProps(
            show = props.data.showEditPopup,
            title = "Edit User",
            onSave = { updatedData =>
              props.dispatch(UserUpdateRequestAction(update = false))
              props.dispatch(props.actions.userUpdate(props.dispatch, updatedData))
            },
            onCancel = { () =>
              props.dispatch(UserUpdateRequestAction(update = false))
            },
            initialData = data
          ))()
        )
      }
    )
  }
}
