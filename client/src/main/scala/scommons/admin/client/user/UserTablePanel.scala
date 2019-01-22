package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.client.ui.page._
import scommons.client.ui.table._
import scommons.react.UiComponent

case class UserTablePanelProps(data: UserState,
                               selectedUserId: Option[Int],
                               onChangeSelect: Int => Unit,
                               onLoadData: (Option[Int], Option[String]) => Unit)

object UserTablePanel extends UiComponent[UserTablePanelProps] {

  protected def create(): ReactClass = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped

    val header = List(
      TableColumnData("Login"),
      TableColumnData("Active"),
      TableColumnData("Logged-in at"),
      TableColumnData("Company"),
      TableColumnData("Updated at")
    )
    
    val dataList = {
      val list = props.data.dataList
      
      // make sure that selected user is added to the list
      list.find(_.id == props.selectedUserId) match {
        case None if props.data.userDetails.exists(_.user.id == props.selectedUserId) =>
          list :+ props.data.userDetails.get.user
        case _ => list
      }
    }

    val rows = dataList.map { data =>
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
        header = header,
        rows = rows,
        selectedIds = props.selectedUserId.map(_.toString).toSet,
        onSelect = { row =>
          props.onChangeSelect(row.id.toInt)
        }
      ))(),

      <(PaginationPanel())(^.wrapped := PaginationPanelProps(
        totalPages = totalPages,
        selectedPage = selectedPage,
        onPage = { page =>
          props.onLoadData(Some(PaginationPanel.toOffset(page, limit)), None)
        }
      ))()
    )
  }
}
