package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.ReactRedux
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminState
import scommons.admin.client.action.ApiActions
import scommons.admin.client.company.action._
import scommons.client.ui.page._
import scommons.client.ui.popup.{InputPopup, InputPopupProps}
import scommons.client.ui.table._
import scommons.client.ui.{Buttons, ButtonsPanel, ButtonsPanelProps}
import scommons.client.util.ActionsData

object CompanyPanelController {

  def apply(): ReactClass = reactClass
  private lazy val reactClass = createComp
  
  private def createComp = ReactRedux.connectAdvanced(
    (dispatch: Dispatch) => {
      (state: AdminState, _: Props[Unit]) => {
        CompanyPanelProps(dispatch, state.companyState)
      }
    }
  )(CompanyPanel())
}

case class CompanyPanelProps(dispatch: Dispatch, state: CompanyState)

object CompanyPanel {

  def apply(): ReactClass = reactClass
  private lazy val reactClass = createComp

  private def createComp = React.createClass[CompanyPanelProps, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.state.dataList.isEmpty) {
        props.dispatch(ApiActions.companyListFetch(props.dispatch, props.state.offset))
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
          props.dispatch(ApiActions.companyListFetch(props.dispatch,
            offset = Some(PaginationPanel.toOffset(page, limit))
          ))
        }))(),
        
        <(InputPopup())(^.wrapped := InputPopupProps(
          props.state.showCreatePopup,
          "Enter Company name:",
          onOk = { text =>
            props.dispatch(CompanyCreateRequestAction(create = false))
            props.dispatch(ApiActions.companyCreate(props.dispatch, text))
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
              props.dispatch(CompanyUpdateRequestAction(update = false))
              props.dispatch(ApiActions.companyUpdate(props.dispatch, data.copy(name = text)))
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
