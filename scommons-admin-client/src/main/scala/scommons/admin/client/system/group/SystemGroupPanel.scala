package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.system.group.action._
import scommons.client.ui._
import scommons.client.ui.popup.{InputPopup, InputPopupProps}

case class SystemGroupPanelProps(dispatch: Dispatch,
                                 actions: SystemGroupActions,
                                 state: SystemGroupState,
                                 selectedId: Option[Int])

object SystemGroupPanel extends UiComponent[SystemGroupPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.state.dataList.isEmpty) {
        props.dispatch(props.actions.systemGroupListFetch(props.dispatch))
      }
    },
    render = { self =>
      val props = self.props.wrapped
      
      val selectedData = props.state.dataList.find(_.id == props.selectedId)
      
      <.div()(
        <(InputPopup())(^.wrapped := InputPopupProps(
          props.state.showCreatePopup,
          "Enter Environment name:",
          onOk = { text =>
            props.dispatch(SystemGroupCreateRequestAction(create = false))
            props.dispatch(props.actions.systemGroupCreate(props.dispatch, text))
          },
          onCancel = { () =>
            props.dispatch(SystemGroupCreateRequestAction(create = false))
          },
          initialValue = "New Environment"
        ))(),
        
        selectedData.map { data =>
          <(InputPopup())(^.wrapped := InputPopupProps(
            props.state.showEditPopup,
            "Enter new Environment name:",
            onOk = { text =>
              props.dispatch(SystemGroupUpdateRequestAction(update = false))
              props.dispatch(props.actions.systemGroupUpdate(props.dispatch, data.copy(name = text)))
            },
            onCancel = { () =>
              props.dispatch(SystemGroupUpdateRequestAction(update = false))
            },
            initialValue = data.name
          ))()
        }
      )
    }
  )
}
