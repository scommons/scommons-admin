package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.client.ui.popup.{InputPopup, InputPopupProps}
import scommons.react._
import scommons.react.hooks._

case class SystemGroupPanelProps(dispatch: Dispatch,
                                 actions: SystemGroupActions,
                                 state: SystemGroupState,
                                 selectedId: Option[Int])

object SystemGroupPanel extends FunctionComponent[SystemGroupPanelProps] {

  private[group] var inputPopupComp: UiComponent[InputPopupProps] = InputPopup

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    
    useEffect({ () =>
      if (props.state.dataList.isEmpty) {
        props.dispatch(props.actions.systemGroupListFetch(props.dispatch))
      }: Unit
    }, Nil)
      
    val selectedData = props.state.dataList.find(_.id == props.selectedId)
    
    <.>()(
      if (props.state.showCreatePopup) Some(
        <(inputPopupComp())(^.wrapped := InputPopupProps(
          message = "Enter Environment name:",
          onOk = { text =>
            props.dispatch(props.actions.systemGroupCreate(props.dispatch, text))
          },
          onCancel = { () =>
            props.dispatch(SystemGroupCreateRequestAction(create = false))
          },
          initialValue = "New Environment"
        ))()
      ) else None,
      
      selectedData.flatMap { data =>
        if (props.state.showEditPopup) Some(
          <(inputPopupComp())(^.wrapped := InputPopupProps(
            message = "Enter new Environment name:",
            onOk = { text =>
              props.dispatch(props.actions.systemGroupUpdate(props.dispatch, data.copy(name = text)))
            },
            onCancel = { () =>
              props.dispatch(SystemGroupUpdateRequestAction(update = false))
            },
            initialValue = data.name
          ))()
        ) else None
      }
    )
  }
}
