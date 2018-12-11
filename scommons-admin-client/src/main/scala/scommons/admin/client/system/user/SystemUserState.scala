package scommons.admin.client.system.user

import scommons.admin.client.api.role.permission.RolePermissionData
import scommons.admin.client.api.system.user.{SystemUserData, SystemUserRoleData}
import scommons.admin.client.system.user.SystemUserActions._

case class SystemUserState(params: SystemUserParams = SystemUserParams(),
                           dataList: List[SystemUserData] = Nil,
                           offset: Option[Int] = None,
                           totalCount: Option[Int] = None,
                           selectedUser: Option[SystemUserData] = None,
                           userRoles: List[SystemUserRoleData] = Nil,
                           permissionsByParentId: Map[Option[Int], List[RolePermissionData]] = Map.empty)

object SystemUserStateReducer {

  def apply(state: Option[SystemUserState], action: Any): SystemUserState = {
    reduce(state.getOrElse(SystemUserState()), action)
  }
  
  private def reduce(state: SystemUserState, action: Any): SystemUserState = action match {
    case SystemUserParamsChangedAction(params) => state.copy(params = params)
    case SystemUserListFetchAction(_, offset) => state.copy(offset = offset)
    case SystemUserListFetchedAction(dataList, totalCount) => state.copy(
      dataList = dataList,
      totalCount = totalCount.orElse(state.totalCount)
    )
    case SystemUserRoleFetchedAction(None) => state.copy(
      selectedUser = None,
      userRoles = Nil,
      permissionsByParentId = Map.empty
    )
    case SystemUserRoleFetchedAction(Some(data)) => state.copy(
      selectedUser = Some(data.systemUser),
      userRoles = data.roles,
      permissionsByParentId = data.permissions.groupBy(_.parentId)
    )
    case SystemUserRoleAddedAction(data) => state.copy(
      selectedUser = Some(data.systemUser),
      userRoles = data.roles,
      permissionsByParentId = data.permissions.groupBy(_.parentId)
    )
    case SystemUserRoleRemovedAction(data) => state.copy(
      selectedUser = Some(data.systemUser),
      userRoles = data.roles,
      permissionsByParentId = data.permissions.groupBy(_.parentId)
    )
    case _ => state
  }
}
