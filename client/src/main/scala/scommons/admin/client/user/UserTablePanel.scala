package scommons.admin.client.user

import scommons.client.ui.page._
import scommons.client.ui.table._
import scommons.react._

case class UserTablePanelProps(data: UserState,
                               selectedUserId: Option[Int],
                               onChangeSelect: Int => Unit,
                               onLoadData: (Option[Int], Option[String]) => Unit)

object UserTablePanel extends FunctionComponent[UserTablePanelProps] {

  private[user] var tablePanelComp: UiComponent[TablePanelProps[String, TableRowData]] = TablePanel
  private[user] var paginationPanelComp: UiComponent[PaginationPanelProps] = PaginationPanel

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped

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
      <(tablePanelComp())(^.wrapped := TablePanelProps(
        header = header,
        rows = rows,
        selectedIds = props.selectedUserId.map(_.toString).toSet,
        onSelect = { row =>
          props.onChangeSelect(row.id.toInt)
        }
      ))(),

      <(paginationPanelComp())(^.wrapped := PaginationPanelProps(
        totalPages = totalPages,
        selectedPage = selectedPage,
        onPage = { page =>
          props.onLoadData(Some(PaginationPanel.toOffset(page, limit)), None)
        }
      ))()
    )
  }
}
