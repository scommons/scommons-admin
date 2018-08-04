package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.role.action._
import scommons.client.ui._
import scommons.client.ui.popup.{InputPopup, InputPopupProps}

case class RolePanelProps(dispatch: Dispatch,
                          actions: RoleActions,
                          state: RoleState,
                          selectedSystemId: Option[Int],
                          selectedId: Option[Int])

object RolePanel extends UiComponent[RolePanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.state.rolesBySystemId.isEmpty) {
        props.dispatch(props.actions.roleListFetch(props.dispatch))
      }
    },
    render = { self =>
      val props = self.props.wrapped

      val selectedData = props.selectedSystemId.flatMap { systemId =>
        props.state.rolesBySystemId.getOrElse(systemId, Nil)
          .find(_.id == props.selectedId)
      }
      
      <.div()(
        props.selectedSystemId.map { systemId =>
          <(InputPopup())(^.wrapped := InputPopupProps(
            props.state.showCreatePopup,
            "Enter Role title:",
            onOk = { text =>
              props.dispatch(RoleCreateRequestAction(create = false))
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
        },
        selectedData.map { data =>
          <(InputPopup())(^.wrapped := InputPopupProps(
            props.state.showEditPopup,
            "Enter new Role title:",
            onOk = { text =>
              props.dispatch(RoleUpdateRequestAction(update = false))
              props.dispatch(props.actions.roleUpdate(props.dispatch, data.copy(title = text)))
            },
            onCancel = { () =>
              props.dispatch(RoleUpdateRequestAction(update = false))
            },
            initialValue = data.title
          ))()
        }
      )
    }
  )
}
