package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.action._
import scommons.client.ui._
import scommons.client.ui.popup.{InputPopup, InputPopupProps}

case class SystemPanelProps(dispatch: Dispatch,
                            actions: SystemActions,
                            state: SystemState,
                            selectedParentId: Option[Int],
                            selectedId: Option[Int])

object SystemPanel extends UiComponent[SystemPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.state.systemsByParentId.isEmpty) {
        props.dispatch(props.actions.systemListFetch(props.dispatch))
      }
    },
    render = { self =>
      val props = self.props.wrapped
      
      val selectedData = props.selectedParentId.flatMap { parentId =>
        props.state.systemsByParentId.getOrElse(parentId, Nil)
          .find(_.id == props.selectedId)
      }
      
      <.div()(
        props.selectedParentId.map { parentId =>
          <(InputPopup())(^.wrapped := InputPopupProps(
            props.state.showCreatePopup,
            "Enter Application name:",
            onOk = { text =>
              props.dispatch(SystemCreateRequestAction(create = false))
              props.dispatch(props.actions.systemCreate(props.dispatch, SystemData(
                id = None,
                name = text,
                password = "",
                title = text,
                url = "http://test.com",
                parentId = parentId
              )))
            },
            onCancel = { () =>
              props.dispatch(SystemCreateRequestAction(create = false))
            },
            initialValue = "New Application"
          ))()
        },
        selectedData.map { data =>
          <(InputPopup())(^.wrapped := InputPopupProps(
            props.state.showEditPopup,
            "Enter new Application name:",
            onOk = { text =>
              props.dispatch(SystemUpdateRequestAction(update = false))
              props.dispatch(props.actions.systemUpdate(props.dispatch, data.copy(name = text)))
            },
            onCancel = { () =>
              props.dispatch(SystemUpdateRequestAction(update = false))
            },
            initialValue = data.name
          ))()
        }
      )
    }
  )
}
