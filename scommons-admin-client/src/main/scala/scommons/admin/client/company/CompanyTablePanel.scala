package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.company.CompanyActions._
import scommons.client.ui.page._
import scommons.client.ui.table._
import scommons.react.UiComponent

case class CompanyTablePanelProps(dispatch: Dispatch,
                                  actions: CompanyActions,
                                  data: CompanyState)

object CompanyTablePanel extends UiComponent[CompanyTablePanelProps] {

  protected def create(): ReactClass = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.data.dataList.isEmpty) {
        props.dispatch(props.actions.companyListFetch(props.dispatch, None, None))
      }
    },
    render = { self =>
      val props = self.props.wrapped
      
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
  
      <.div()(
        <(TablePanel())(^.wrapped := TablePanelProps(
          header = header,
          rows = rows,
          selectedIds = props.data.selectedId.map(_.toString).toSet,
          onSelect = { row =>
            props.dispatch(CompanySelectedAction(row.id.toInt))
          }
        ))(),
        
        <(PaginationPanel())(^.wrapped := PaginationPanelProps(totalPages, selectedPage, onPage = { page =>
          props.dispatch(props.actions.companyListFetch(props.dispatch,
            offset = Some(PaginationPanel.toOffset(page, limit)),
            symbols = None
          ))
        }))()
      )
    }
  )
}
