package scommons.admin.client.role.permission

import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.role.permission.RolePermissionData
import scommons.admin.client.role.permission.RolePermissionActions._

case class RolePermissionState(permissionsByParentId: Map[Option[Int], List[RolePermissionData]] = Map.empty,
                               role: Option[RoleData] = None) {

  def getPermissions(parentId: Option[Int]): List[RolePermissionData] = {
    permissionsByParentId.getOrElse(parentId, Nil)
  }
}

object RolePermissionStateReducer {

  def apply(state: Option[RolePermissionState], action: Any): RolePermissionState = {
    reduce(state.getOrElse(RolePermissionState()), action)
  }
  
  private def reduce(state: RolePermissionState, action: Any): RolePermissionState = action match {
    case RolePermissionFetchedAction(data) => state.copy(
      permissionsByParentId = data.permissions.groupBy(_.parentId),
      role = Some(data.role)
    )
    case RolePermissionAddedAction(data) => state.copy(
      permissionsByParentId = data.permissions.groupBy(_.parentId),
      role = Some(data.role)
    )
    case RolePermissionRemovedAction(data) => state.copy(
      permissionsByParentId = data.permissions.groupBy(_.parentId),
      role = Some(data.role)
    )
    case _ => state
  }
}
