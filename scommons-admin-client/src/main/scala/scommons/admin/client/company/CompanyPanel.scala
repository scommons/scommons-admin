package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.company.CompanyActions._
import scommons.client.ui._
import scommons.client.ui.page._
import scommons.client.ui.popup.{InputPopup, InputPopupProps}
import scommons.client.ui.table._
import scommons.client.util.ActionsData

case class CompanyPanelProps(dispatch: Dispatch,
                             actions: CompanyActions,
                             state: CompanyState)

object CompanyPanel extends UiComponent[CompanyPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.state.dataList.isEmpty) {
        props.dispatch(props.actions.companyListFetch(props.dispatch, None, None))
      }
    },
    render = { self =>
      val props = self.props.wrapped
      
      val header = List(
        TableColumnData("Id"),
        TableColumnData("Company Name")
      )
  
      val rows = props.state.dataList.map { data =>
        val id = data.id.getOrElse(0).toString
        TableRowData(id, List(id, data.name))
      }
      
      val selectedData = props.state.dataList.find(_.id == props.state.selectedId)

      val limit = CompanyActions.listLimit
      val totalPages = PaginationPanel.toTotalPages(props.state.totalCount.getOrElse(0), limit)
      val selectedPage = math.min(totalPages, PaginationPanel.toPage(props.state.offset.getOrElse(0), limit))
  
      <.div()(
        <(ButtonsPanel())(^.wrapped := ButtonsPanelProps(
          List(Buttons.ADD, Buttons.EDIT),
          ActionsData(Set(Buttons.ADD.command) ++ selectedData.map(_ => Buttons.EDIT.command), dispatch => {
            case Buttons.ADD.command => dispatch(CompanyCreateRequestAction(create = true))
            case Buttons.EDIT.command => dispatch(CompanyUpdateRequestAction(update = true))
          }),
          props.dispatch
        ))(),
  
        <(TablePanel())(^.wrapped := TablePanelProps(
          header,
          rows,
          props.state.selectedId.map(_.toString).toSet,
          onSelect = { row =>
            props.dispatch(CompanySelectedAction(row.id.toInt))
          }
        ))(),
        
        <(PaginationPanel())(^.wrapped := PaginationPanelProps(totalPages, selectedPage, onPage = { page =>
          props.dispatch(props.actions.companyListFetch(props.dispatch,
            offset = Some(PaginationPanel.toOffset(page, limit)),
            symbols = None
          ))
        }))(),
        
        <(InputPopup())(^.wrapped := InputPopupProps(
          props.state.showCreatePopup,
          "Enter Company name:",
          onOk = { text =>
            props.dispatch(props.actions.companyCreate(props.dispatch, text))
          },
          onCancel = { () =>
            props.dispatch(CompanyCreateRequestAction(create = false))
          },
          initialValue = "New Company"
        ))(),
  
        selectedData.map { data =>
          <(InputPopup())(^.wrapped := InputPopupProps(
            props.state.showEditPopup,
            "Enter new Company name:",
            onOk = { text =>
              props.dispatch(props.actions.companyUpdate(props.dispatch, data.copy(name = text)))
            },
            onCancel = { () =>
              props.dispatch(CompanyUpdateRequestAction(update = false))
            },
            initialValue = data.name
          ))()
        }
      )
    }
  )
}
