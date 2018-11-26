package scommons.admin.client.user.system

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.user.UserData
import scommons.admin.client.api.user.system.UserSystemUpdateReq
import scommons.client.ui._
import scommons.client.ui.list._

case class UserSystemPanelProps(dispatch: Dispatch,
                                actions: UserSystemActions,
                                systemData: UserSystemState,
                                selectedUser: Option[UserData])

object UserSystemPanel extends UiComponent[UserSystemPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit](
    componentDidMount = { self =>
      val props = self.props.wrapped
      val selectedUserId = props.selectedUser.flatMap(_.id)
      if (props.systemData.userId != selectedUserId && selectedUserId.isDefined) {
        props.dispatch(props.actions.userSystemsFetch(props.dispatch, selectedUserId.get))
      }
    },
    componentDidUpdate = { (self, _, _) =>
      val props = self.props.wrapped
      val selectedUserId = props.selectedUser.flatMap(_.id)
      if (props.systemData.userId != selectedUserId && selectedUserId.isDefined) {
        props.dispatch(props.actions.userSystemsFetch(props.dispatch, selectedUserId.get))
      }
    },
    render = { self =>
      val props = self.props.wrapped

      <(PickList())(^.wrapped := PickListProps(
        items = props.systemData.systems.map { s =>
          ListBoxData(s.id.toString, s.name, Some(AdminImagesCss.computer))
        },
        selectedIds = props.systemData.systems.filter(_.isSelected).map(_.id.toString).toSet,
        onSelectChange = { (ids, isAdd) =>
          props.selectedUser.map(u => (u.id, u.version)).collect {
            case (Some(id), Some(version)) => (id, version)
          }.foreach { case (userId, userVersion) =>
            if (isAdd) {
              props.dispatch(props.actions.userSystemsAdd(
                props.dispatch,
                userId,
                UserSystemUpdateReq(ids.map(_.toInt), userVersion)
              ))
            }
            else {
              props.dispatch(props.actions.userSystemsRemove(
                props.dispatch,
                userId,
                UserSystemUpdateReq(ids.map(_.toInt), userVersion)
              ))
            }
          }
        },
        sourceTitle = "Available apps",
        destTitle = "Assigned apps"
      ))()
    }
  )
}
