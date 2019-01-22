package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.client.ui.page._
import scommons.client.ui.table._
import scommons.react.UiComponent

case class SystemUserTablePanelProps(data: SystemUserState,
                                     selectedUserId: Option[Int],
                                     onChangeSelect: Int => Unit,
                                     onLoadData: (Option[Int], Option[String]) => Unit)

object SystemUserTablePanel extends UiComponent[SystemUserTablePanelProps] {

  protected def create(): ReactClass = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped

    val header = List(
      TableColumnData("Login"),
      TableColumnData("Logged-in at"),
      TableColumnData("Created at"),
      TableColumnData("Updated at")
    )
    
    val dataList = {
      val list = props.data.dataList

      // make sure that selected user is added to the list
      props.data.selectedUser.filter(su => props.selectedUserId.contains(su.userId)) match {
        case None => list
        case Some(selectedUser) =>
          list.find(su => su.userId == selectedUser.userId) match {
            case None => list :+ selectedUser
            case _ => list.map {
              case su if su.userId == selectedUser.userId => selectedUser
              case su => su
            }
          }
      }
    }

    val rows = dataList.map { data =>
      val id = data.userId.toString
      TableRowData(id, List(
        data.login,
        data.lastLoginDate.map(_.toString()).getOrElse(""),
        data.createdAt.toString(),
        data.updatedAt.toString()
      ))
    }

    val limit = SystemUserActions.listLimit
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
