package scommons.admin.client.company

import scommons.client.ui.page._
import scommons.client.ui.table._
import scommons.react._

case class CompanyTablePanelProps(data: CompanyState,
                                  onChangeSelect: Int => Unit,
                                  onLoadData: (Option[Int], Option[String]) => Unit)

object CompanyTablePanel extends FunctionComponent[CompanyTablePanelProps] {

  private[company] var tablePanel: UiComponent[TablePanelProps[String, TableRowData]] = TablePanel
  private[company] var paginationPanel: UiComponent[PaginationPanelProps] = PaginationPanel

  protected def render(selfProps: Props): ReactElement = {
    val props = selfProps.wrapped
    
    val header = List(
      TableColumnData("Id"),
      TableColumnData("Company Name")
    )

    val rows = props.data.dataList.map { data =>
      val id = data.id.getOrElse(0).toString
      TableRowData(id, List(id, data.name))
    }
    
    val limit = CompanyActions.listLimit
    val totalPages = PaginationPanel.toTotalPages(props.data.totalCount.getOrElse(0), limit)
    val selectedPage = math.min(totalPages, PaginationPanel.toPage(props.data.offset.getOrElse(0), limit))

    <.>()(
      <(tablePanel())(^.wrapped := TablePanelProps(
        header = header,
        rows = rows,
        selectedIds = props.data.selectedId.map(_.toString).toSet,
        onSelect = { row =>
          props.onChangeSelect(row.id.toInt)
        }
      ))(),
      
      <(paginationPanel())(^.wrapped := PaginationPanelProps(
        totalPages = totalPages,
        selectedPage = selectedPage,
        onPage = { page =>
          props.onLoadData(Some(PaginationPanel.toOffset(page, limit)), None)
        }
      ))()
    )
  }
}
