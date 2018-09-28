package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.SystemActions._
import scommons.client.ui._

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
        props.state.getSystems(parentId)
          .find(_.id == props.selectedId)
      }
      
      <.div()(
        props.selectedParentId.map { parentId =>
          <(SystemEditPopup())(^.wrapped := SystemEditPopupProps(
            show = props.state.showCreatePopup,
            title = "New Application",
            initialData = SystemData(
              id = None,
              name = "",
              password = "",
              title = "",
              url = "",
              parentId = parentId
            ),
            onSave = { data =>
              props.dispatch(props.actions.systemCreate(props.dispatch, data))
            },
            onCancel = { () =>
              props.dispatch(SystemCreateRequestAction(create = false))
            }
          ))()
        },
        selectedData.map { data =>
          <(SystemEditPanel())(^.wrapped := SystemEditPanelProps(
            readOnly = true,
            initialData = data,
            requestFocus = false,
            onChange = _ => (),
            onEnter = () => ()
          ))()
        },
        selectedData.map { data =>
          <(SystemEditPopup())(^.wrapped := SystemEditPopupProps(
            show = props.state.showEditPopup,
            title = "Edit Application",
            initialData = data,
            onSave = { updatedData =>
              props.dispatch(props.actions.systemUpdate(props.dispatch, updatedData))
            },
            onCancel = { () =>
              props.dispatch(SystemUpdateRequestAction(update = false))
            }
          ))()
        }
      )
    }
  )
}
