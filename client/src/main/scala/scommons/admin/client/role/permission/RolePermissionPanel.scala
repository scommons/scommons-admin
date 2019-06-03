package scommons.admin.client.role.permission

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.role.permission.{RolePermissionData, RolePermissionUpdateReq}
import scommons.client.ui._
import scommons.client.ui.tree._
import scommons.react._
import scommons.react.hooks._

case class RolePermissionPanelProps(dispatch: Dispatch,
                                    actions: RolePermissionActions,
                                    state: RolePermissionState,
                                    selectedRoleId: Int)

object RolePermissionPanel extends FunctionComponent[RolePermissionPanelProps] {

  protected def render(selfProps: Props): ReactElement = {
    val props = selfProps.wrapped
    
    useEffect({ () =>
      if (!props.state.role.flatMap(_.id).contains(props.selectedRoleId)) {
        props.dispatch(props.actions.rolePermissionsFetch(props.dispatch, props.selectedRoleId))
      }: Unit
    }, List(props.selectedRoleId))

    val roots = buildTree(props.state.permissionsByParentId)

    <(CheckBoxTree())(^.wrapped := CheckBoxTreeProps(
      roots = roots,
      onChange = { (data, value) =>
        val permissionId = data.key.toInt
        val ids = getAllDescendantIds(permissionId, props.state.permissionsByParentId)
        
        props.state.role.flatMap(_.version).foreach { roleVersion =>
          if (TriState.isSelected(value)) {
            props.dispatch(props.actions.rolePermissionsAdd(
              props.dispatch,
              props.selectedRoleId,
              RolePermissionUpdateReq(ids, roleVersion)
            ))
          }
          else {
            props.dispatch(props.actions.rolePermissionsRemove(
              props.dispatch,
              props.selectedRoleId,
              RolePermissionUpdateReq(ids, roleVersion)
            ))
          }
        }
      },
      openNodes = roots.map(_.key).toSet
    ))()
  }

  private[permission] def getAllDescendantIds(id: Int,
                                              permissions: Map[Option[Int], List[RolePermissionData]]): Set[Int] = {

    def loop(dataList: List[RolePermissionData], ids: Set[Int]): Set[Int] = dataList match {
      case Nil => ids
      case head :: tail =>
        val results = loop(tail, ids + head.id)
        if (head.isNode) {
          val children = permissions.getOrElse(Some(head.id), Nil)
          results ++ loop(children, Set.empty)
        }
        else results
    }
    
    loop(permissions.getOrElse(Some(id), Nil), Set(id))
  }
  
  def buildTree(permissions: Map[Option[Int], List[RolePermissionData]]): List[CheckBoxTreeData] = {

    def loop(dataList: List[RolePermissionData]): List[CheckBoxTreeData] = dataList.map { p =>
      val key = p.id.toString
      val value = if (p.isEnabled) TriState.Selected else TriState.Deselected
      val text = p.title
      if (p.isNode) {
        val children = loop(permissions.getOrElse(Some(p.id), Nil))
        CheckBoxTreeNodeData(key, CheckBoxTreeData.calcNodeValue(children, value), text, None, children)
      }
      else CheckBoxTreeItemData(key, value, text, Some(AdminImagesCss.keySmall))
    }

    loop(permissions.getOrElse(None, Nil))
  }
}
