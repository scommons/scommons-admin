package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.company.CompanyActions._
import scommons.client.ui._
import scommons.client.ui.popup.{InputPopup, InputPopupProps}
import scommons.client.util.ActionsData
import scommons.react._

case class CompanyPanelProps(dispatch: Dispatch,
                             actions: CompanyActions,
                             data: CompanyState)

object CompanyPanel extends ClassComponent[CompanyPanelProps] {

  protected def create(): ReactClass = createClass[Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.data.dataList.isEmpty) {
        props.dispatch(props.actions.companyListFetch(props.dispatch, None, None))
      }
    },
    render = { self =>
      val props = self.props.wrapped
      val selectedData = props.data.dataList.find(_.id == props.data.selectedId)
  
      <.>()(
        <(ButtonsPanel())(^.wrapped := ButtonsPanelProps(
          List(Buttons.ADD, Buttons.EDIT),
          ActionsData(Set(Buttons.ADD.command) ++ selectedData.map(_ => Buttons.EDIT.command), dispatch => {
            case Buttons.ADD.command => dispatch(CompanyCreateRequestAction(create = true))
            case Buttons.EDIT.command => dispatch(CompanyUpdateRequestAction(update = true))
          }),
          props.dispatch
        ))(),
  
        <(CompanyTablePanel())(^.wrapped := CompanyTablePanelProps(
          data = props.data,
          onChangeSelect = { companyId =>
            props.dispatch(CompanySelectedAction(companyId))
          },
          onLoadData = { (offset, symbols) =>
            props.dispatch(props.actions.companyListFetch(props.dispatch, offset, symbols))
          }
        ))(),
        
        <(InputPopup())(^.wrapped := InputPopupProps(
          props.data.showCreatePopup,
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
            props.data.showEditPopup,
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
