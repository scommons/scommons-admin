package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.SystemActions._
import scommons.react._
import scommons.react.hooks._

case class SystemPanelProps(dispatch: Dispatch,
                            actions: SystemActions,
                            state: SystemState,
                            selectedParentId: Option[Int],
                            selectedId: Option[Int])

object SystemPanel extends FunctionComponent[SystemPanelProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    
    useEffect({ () =>
      if (props.state.systemsByParentId.isEmpty) {
        props.dispatch(props.actions.systemListFetch(props.dispatch))
      }: Unit
    }, Nil)
    
    val selectedData = props.selectedParentId.flatMap { parentId =>
      props.state.getSystems(parentId).find(_.id == props.selectedId)
    }
    
    <.>()(
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
}
