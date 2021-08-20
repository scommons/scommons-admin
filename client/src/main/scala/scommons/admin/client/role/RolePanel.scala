package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.role.RoleActions._
import scommons.client.ui.popup.{InputPopup, InputPopupProps}
import scommons.react._
import scommons.react.hooks._

case class RolePanelProps(dispatch: Dispatch,
                          actions: RoleActions,
                          state: RoleState,
                          selectedSystemId: Option[Int],
                          selectedId: Option[Int])

object RolePanel extends FunctionComponent[RolePanelProps] {

  private[role] var inputPopupComp: UiComponent[InputPopupProps] = InputPopup

  protected def render(selfProps: Props): ReactElement = {
    val props = selfProps.wrapped
    
    useEffect({ () =>
      if (props.state.rolesBySystemId.isEmpty) {
        props.dispatch(props.actions.roleListFetch(props.dispatch))
      }: Unit
    }, Nil)

    val selectedData = props.selectedSystemId.flatMap { systemId =>
      props.state.getRoles(systemId)
        .find(_.id == props.selectedId)
    }
    
    <.>()(
      props.selectedSystemId.flatMap { systemId =>
        if (props.state.showCreatePopup) Some(
          <(inputPopupComp())(^.wrapped := InputPopupProps(
            message = "Enter Role title:",
            onOk = { text =>
              props.dispatch(props.actions.roleCreate(props.dispatch, RoleData(
                id = None,
                systemId = systemId,
                title = text
              )))
            },
            onCancel = { () =>
              props.dispatch(RoleCreateRequestAction(create = false))
            },
            initialValue = "NEW_ROLE"
          ))()
        ) else None
      },
      
      selectedData.flatMap { data =>
        if (props.state.showEditPopup) Some(
          <(inputPopupComp())(^.wrapped := InputPopupProps(
            message = "Enter new Role title:",
            onOk = { text =>
              props.dispatch(props.actions.roleUpdate(props.dispatch, data.copy(title = text)))
            },
            onCancel = { () =>
              props.dispatch(RoleUpdateRequestAction(update = false))
            },
            initialValue = data.title
          ))()
        ) else None
      }
    )
  }
}
