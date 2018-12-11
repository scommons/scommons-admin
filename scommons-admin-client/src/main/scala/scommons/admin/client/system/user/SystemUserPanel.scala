package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminImagesCss
import scommons.client.ui._
import scommons.client.ui.tab.{TabItemData, TabPanel, TabPanelProps}

import scala.concurrent.ExecutionContext.Implicits.global

case class SystemUserPanelProps(dispatch: Dispatch,
                                actions: SystemUserActions,
                                data: SystemUserState,
                                selectedParams: SystemUserParams,
                                onChangeParams: SystemUserParams => Unit)

object SystemUserPanel extends UiComponent[SystemUserPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      props.selectedParams.systemId.foreach { systemId =>
        if (!props.data.params.systemId.contains(systemId)) {
          props.dispatch(props.actions.systemUserListFetch(props.dispatch, systemId, None, None))
        }
        props.selectedParams.userId.foreach { userId =>
          if (!props.data.params.systemId.contains(systemId) || !props.data.params.userId.contains(userId)) {
            props.dispatch(props.actions.systemUserRolesFetch(props.dispatch, systemId, userId))
          }
        }
      }
      if (props.selectedParams != props.data.params) {
        props.onChangeParams(props.selectedParams)
      }
    },
    componentDidUpdate = { (self, prevProps, _) =>
      val props = self.props.wrapped
      if (props.selectedParams != prevProps.wrapped.selectedParams) {
        props.selectedParams.systemId.foreach { systemId =>
          if (!props.data.params.systemId.contains(systemId)) {
            props.dispatch(props.actions.systemUserListFetch(props.dispatch, systemId, None, None))
          }
          props.selectedParams.userId.foreach { userId =>
            if (!props.data.params.systemId.contains(systemId) || !props.data.params.userId.contains(userId)) {
              props.dispatch(props.actions.systemUserRolesFetch(props.dispatch, systemId, userId))
            }
          }
        }
        if (props.selectedParams != props.data.params) {
          props.onChangeParams(props.selectedParams)
        }
      }
    },
    render = { self =>
      val props = self.props.wrapped

      <.div()(props.selectedParams.systemId.toList.flatMap { systemId =>
        <(SystemUserTablePanel())(^.wrapped := SystemUserTablePanelProps(
          data = props.data,
          selectedUserId = props.selectedParams.userId,
          onChangeSelect = { userId =>
            val fetchAction = props.actions.systemUserRolesFetch(props.dispatch, systemId, userId)
            fetchAction.task.future.foreach { _ =>
              props.onChangeParams(props.selectedParams.copy(userId = Some(userId)))
            }
            props.dispatch(fetchAction)
          },
          onLoadData = { (offset, symbols) =>
            props.onChangeParams(props.selectedParams.copy(userId = None))
            props.dispatch(props.actions.systemUserListFetch(props.dispatch, systemId, offset, symbols))
          }
        ))() +: props.data.selectedUser.toList.map { _ =>
          <(TabPanel())(^.wrapped := TabPanelProps(
            items = List(
              TabItemData("Permissions", image = Some(AdminImagesCss.key), render = Some({ _ =>
                <(SystemUserRolePanel())(^.wrapped := SystemUserRolePanelProps(
                  dispatch = props.dispatch,
                  actions = props.actions,
                  data = props.data,
                  systemId = systemId
                ))()
              }))
            )
          ))()
        }
      })
    }
  )
}
