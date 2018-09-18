package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.user.UserActions.UserSelectedAction
import scommons.client.ui._
import scommons.client.ui.page._
import scommons.client.ui.table._

case class UserTablePanelProps(dispatch: Dispatch,
                               actions: UserActions,
                               data: UserState)

object UserTablePanel extends UiComponent[UserTablePanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.data.dataList.isEmpty) {
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
  
      val rows = props.data.dataList.map { data =>
        val id = data.id.getOrElse(0).toString
        TableRowData(id, List(
          data.login,
          data.active.toString,
          data.lastLoginDate.map(_.toString()).getOrElse(""),
          data.company.name,
          data.updatedAt.map(_.toString()).getOrElse("")
        ))
      }
  
      val limit = UserActions.listLimit
      val totalPages = PaginationPanel.toTotalPages(props.data.totalCount.getOrElse(0), limit)
      val selectedPage = math.min(totalPages, PaginationPanel.toPage(props.data.offset.getOrElse(0), limit))
  
      <.div()(
        <(TablePanel())(^.wrapped := TablePanelProps(
          header,
          rows,
          props.data.selectedId.map(_.toString).toSet,
          onSelect = { row =>
            props.dispatch(UserSelectedAction(row.id.toInt))
          }
        ))(),
  
        <(PaginationPanel())(^.wrapped := PaginationPanelProps(totalPages, selectedPage, onPage = { page =>
          props.dispatch(props.actions.userListFetch(props.dispatch,
            offset = Some(PaginationPanel.toOffset(page, limit)),
            symbols = None
          ))
        }))()
      )
    }
  )
}
