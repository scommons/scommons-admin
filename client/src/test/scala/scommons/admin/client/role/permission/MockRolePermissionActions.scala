package scommons.admin.client.role.permission

import scommons.admin.client.api.role.permission._
import scommons.admin.client.role.permission.RolePermissionActions._
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockRolePermissionActions(
  rolePermissionsFetchMock: (Dispatch, Int) => RolePermissionFetchAction = (_, _) => ???,
  rolePermissionsAddMock: (Dispatch, Int, RolePermissionUpdateReq) => RolePermissionAddAction = (_, _, _) => ???,
  rolePermissionsRemoveMock: (Dispatch, Int, RolePermissionUpdateReq) => RolePermissionRemoveAction = (_, _, _) => ???
) extends RolePermissionActions {

  override protected def client: RolePermissionApi = ???
  
  override def rolePermissionsFetch(dispatch: Dispatch, roleId: Int): RolePermissionFetchAction =
    rolePermissionsFetchMock(dispatch, roleId)
    
  override def rolePermissionsAdd(dispatch: Dispatch, roleId: Int, data: RolePermissionUpdateReq): RolePermissionAddAction =
    rolePermissionsAddMock(dispatch, roleId, data)
    
  override def rolePermissionsRemove(dispatch: Dispatch, roleId: Int, data: RolePermissionUpdateReq): RolePermissionRemoveAction =
    rolePermissionsRemoveMock(dispatch, roleId, data)
}
