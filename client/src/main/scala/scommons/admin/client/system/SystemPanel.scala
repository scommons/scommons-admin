package scommons.admin.client.system

import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.SystemActions._
import scommons.react._
import scommons.react.hooks._
import scommons.react.redux.Dispatch

case class SystemPanelProps(dispatch: Dispatch,
                            actions: SystemActions,
                            state: SystemState,
                            selectedParentId: Option[Int],
                            selectedId: Option[Int])

object SystemPanel extends FunctionComponent[SystemPanelProps] {

  private[system] var systemEditPopup: UiComponent[SystemEditPopupProps] = SystemEditPopup
  private[system] var systemEditPanel: UiComponent[SystemEditPanelProps] = SystemEditPanel

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
      props.selectedParentId.flatMap { parentId =>
        if (props.state.showCreatePopup) Some(
          <(systemEditPopup())(^.wrapped := SystemEditPopupProps(
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
        ) else None
      },
      selectedData.map { data =>
        <(systemEditPanel())(^.wrapped := SystemEditPanelProps(
          readOnly = true,
          initialData = data,
          requestFocus = false,
          onChange = _ => (),
          onEnter = () => ()
        ))()
      },
      selectedData.flatMap { data =>
        if (props.state.showEditPopup) Some(
          <(systemEditPopup())(^.wrapped := SystemEditPopupProps(
            title = "Edit Application",
            initialData = data,
            onSave = { updatedData =>
              props.dispatch(props.actions.systemUpdate(props.dispatch, updatedData))
            },
            onCancel = { () =>
              props.dispatch(SystemUpdateRequestAction(update = false))
            }
          ))()
        ) else None
      }
    )
  }
}
