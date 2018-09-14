package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions._
import scommons.client.ui._
import scommons.client.ui.page._
import scommons.client.ui.popup.{InputPopup, InputPopupProps}
import scommons.client.ui.table._
import scommons.client.util.ActionsData

case class UserPanelProps(dispatch: Dispatch,
                          actions: UserActions,
                          state: UserState)

object UserPanel extends UiComponent[UserPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.state.dataList.isEmpty) {
        props.dispatch(props.actions.userListFetch(props.dispatch, None, None))
      }
    },
    render = { self =>
      val props = self.props.wrapped
      
      val header = List(
        TableColumnData("Login"),
        TableColumnData("Active"),
        TableColumnData("Logged-in at"),
        TableColumnData("Company"),
        TableColumnData("Updated at")
      )
  
      val rows = props.state.dataList.map { data =>
        val id = data.id.getOrElse(0).toString
        TableRowData(id, List(
          data.login,
          data.active.toString,
          data.lastLoginDate.map(_.toString()).getOrElse(""),
          data.company.name,
          data.updatedAt.map(_.toString()).getOrElse("")
        ))
      }
      
      val selectedData = props.state.dataList.find(_.id == props.state.selectedId)

      val limit = UserActions.listLimit
      val totalPages = PaginationPanel.toTotalPages(props.state.totalCount.getOrElse(0), limit)
      val selectedPage = math.min(totalPages, PaginationPanel.toPage(props.state.offset.getOrElse(0), limit))
  
      <.div()(
        <(ButtonsPanel())(^.wrapped := ButtonsPanelProps(
          List(Buttons.ADD, Buttons.EDIT),
          ActionsData(Set(Buttons.ADD.command) ++ selectedData.map(_ => Buttons.EDIT.command), dispatch => {
            case Buttons.ADD.command => dispatch(UserCreateRequestAction(create = true))
            case Buttons.EDIT.command => dispatch(UserUpdateRequestAction(update = true))
          }),
          props.dispatch
        ))(),
  
        <(TablePanel())(^.wrapped := TablePanelProps(
          header,
          rows,
          props.state.selectedId.map(_.toString).toSet,
          onSelect = { row =>
            props.dispatch(UserSelectedAction(row.id.toInt))
          }
        ))(),
        
        <(PaginationPanel())(^.wrapped := PaginationPanelProps(totalPages, selectedPage, onPage = { page =>
          props.dispatch(props.actions.userListFetch(props.dispatch,
            offset = Some(PaginationPanel.toOffset(page, limit)),
            symbols = None
          ))
        }))(),
        
        <(InputPopup())(^.wrapped := InputPopupProps(
          props.state.showCreatePopup,
          "Enter User login:",
          onOk = { text =>
            props.dispatch(UserCreateRequestAction(create = false))
            props.dispatch(props.actions.userCreate(props.dispatch, UserDetailsData(
              user = UserData(
                id = None,
                company = UserCompanyData(1, "Test Company"),
                login = text,
                password = "test",
                active = true
              ),
              profile = UserProfileData(
                email = s"$text",
                firstName = "Firstname",
                lastName = "Lastname",
                phone = Some("0123 456 789")
              )
            )))
          },
          onCancel = { () =>
            props.dispatch(UserCreateRequestAction(create = false))
          },
          initialValue = "New User"
        ))(),
  
        selectedData.map { data =>
          <(InputPopup())(^.wrapped := InputPopupProps(
            props.state.showEditPopup,
            "Enter new User login:",
            onOk = { text =>
              props.dispatch(UserUpdateRequestAction(update = false))
              props.dispatch(props.actions.userUpdate(props.dispatch, UserDetailsData(
                user = data.copy(
                  login = text
                ),
                profile = UserProfileData(
                  email = s"$text",
                  firstName = "Firstname",
                  lastName = "Lastname",
                  phone = Some("0123 456 789"),
                  version = data.version
                )
              )))
            },
            onCancel = { () =>
              props.dispatch(UserUpdateRequestAction(update = false))
            },
            initialValue = data.login
          ))()
        }
      )
    }
  )
}
