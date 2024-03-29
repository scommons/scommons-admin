package scommons.admin.client.system.user

import scommons.admin.client.AdminImagesCss
import scommons.client.ui.tab._
import scommons.react._
import scommons.react.redux.Dispatch

import scala.concurrent.ExecutionContext.Implicits.global

case class SystemUserPanelProps(dispatch: Dispatch,
                                actions: SystemUserActions,
                                data: SystemUserState,
                                selectedParams: SystemUserParams,
                                onChangeParams: SystemUserParams => Unit)

object SystemUserPanel extends ClassComponent[SystemUserPanelProps] {

  private[user] var systemUserTablePanel: UiComponent[SystemUserTablePanelProps] = SystemUserTablePanel
  private[user] var tabPanelComp: UiComponent[TabPanelProps] = TabPanel
  private[user] var systemUserRolePanel: UiComponent[SystemUserRolePanelProps] = SystemUserRolePanel

  protected def create(): ReactClass = createClass[Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      props.selectedParams.systemId.foreach { systemId =>
        if (!props.data.params.systemId.contains(systemId)) {
          props.dispatch(props.actions.systemUserListFetch(props.dispatch, systemId, None, None))
        }
        props.selectedParams.userId.foreach { userId =>
          if (props.selectedParams != props.data.params) {
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
            if (props.selectedParams != props.data.params) {
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

      <.>()(props.selectedParams.systemId.toList.flatMap { systemId =>
        <(systemUserTablePanel())(^.wrapped := SystemUserTablePanelProps(
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
          <(tabPanelComp())(^.wrapped := TabPanelProps(
            items = List(
              TabItemData("Permissions", image = Some(AdminImagesCss.key), render = Some({ _ =>
                <(systemUserRolePanel())(^.wrapped := SystemUserRolePanelProps(
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
