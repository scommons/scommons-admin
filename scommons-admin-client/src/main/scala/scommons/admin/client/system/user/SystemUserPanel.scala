package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.client.ui._

case class SystemUserPanelProps(dispatch: Dispatch,
                                actions: SystemUserActions,
                                data: SystemUserState,
                                selectedSystemId: Option[Int])

object SystemUserPanel extends UiComponent[SystemUserPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      if (props.data.systemId != props.selectedSystemId && props.selectedSystemId.isDefined) {
        props.dispatch(props.actions.systemUserListFetch(props.dispatch, props.selectedSystemId.get, None, None))
      }
    },
    componentDidUpdate = { (self, prevProps, _) =>
      val props = self.props.wrapped
      if (props.selectedSystemId != prevProps.wrapped.selectedSystemId) {
        if (props.data.systemId != props.selectedSystemId && props.selectedSystemId.isDefined) {
          props.dispatch(props.actions.systemUserListFetch(props.dispatch, props.selectedSystemId.get, None, None))
        }
      }
    },
    render = { self =>
      val props = self.props.wrapped

      <.div()(props.selectedSystemId.map { systemId =>
        <(SystemUserTablePanel())(^.wrapped := SystemUserTablePanelProps(
          data = props.data,
          selectedUserId = None,
          onChangeSelect = { _ =>
          },
          onLoadData = { (offset, symbols) =>
            props.dispatch(props.actions.systemUserListFetch(props.dispatch, systemId, offset, symbols))
          }
        ))()
      })
    }
  )
}
