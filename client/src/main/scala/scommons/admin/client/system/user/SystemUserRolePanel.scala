package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.system.user.SystemUserRoleUpdateReq
import scommons.admin.client.role.permission.RolePermissionPanel
import scommons.client.ui.list.{ListBoxData, PickList, PickListProps}
import scommons.client.ui.tree.{CheckBoxTree, CheckBoxTreeProps}
import scommons.react.UiComponent

case class SystemUserRolePanelProps(dispatch: Dispatch,
                                    actions: SystemUserActions,
                                    data: SystemUserState,
                                    systemId: Int)

object SystemUserRolePanel extends UiComponent[SystemUserRolePanelProps] {

  protected def create(): ReactClass = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped
    
    val roots = RolePermissionPanel.buildTree(props.data.permissionsByParentId)
    
    <.div(^.className := "row-fluid")(
      <.div(^.className := "span6")(
        <(PickList())(^.wrapped := PickListProps(
          items = props.data.userRoles.map { r =>
            ListBoxData(r.id.toString, r.title, Some(AdminImagesCss.role))
          },
          selectedIds = props.data.userRoles.filter(_.isSelected).map(_.id.toString).toSet,
          onSelectChange = { (ids, isAdd) =>
            props.data.selectedUser.foreach { su =>
              if (isAdd) {
                props.dispatch(props.actions.systemUserRolesAdd(
                  props.dispatch,
                  props.systemId,
                  su.userId,
                  SystemUserRoleUpdateReq(ids.map(_.toInt), su.version)
                ))
              }
              else {
                props.dispatch(props.actions.systemUserRolesRemove(
                  props.dispatch,
                  props.systemId,
                  su.userId,
                  SystemUserRoleUpdateReq(ids.map(_.toInt), su.version)
                ))
              }
            }
          },
          sourceTitle = "Available Roles",
          destTitle = "Assigned Roles"
        ))()
      ),
      <.div(^.className := "span6")(
        <(CheckBoxTree())(^.wrapped := CheckBoxTreeProps(
          roots = roots,
          readOnly = true,
          openNodes = roots.map(_.key).toSet
        ))()
      )
    )
  }
}
