package scommons.admin.client.role.permission

import scommons.admin.client.api.role.permission._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockRolePermissionApi(
  listRolePermissionsMock: Int => Future[RolePermissionResp] = _ => ???,
  addRolePermissionsMock: (Int, RolePermissionUpdateReq) => Future[RolePermissionResp] = (_, _) => ???,
  removeRolePermissionsMock: (Int, RolePermissionUpdateReq) => Future[RolePermissionResp] = (_, _) => ???
) extends RolePermissionApi {

  def listRolePermissions(roleId: Int): Future[RolePermissionResp] =
    listRolePermissionsMock(roleId)

  def addRolePermissions(roleId: Int, data: RolePermissionUpdateReq): Future[RolePermissionResp] =
    addRolePermissionsMock(roleId, data)

  def removeRolePermissions(roleId: Int, data: RolePermissionUpdateReq): Future[RolePermissionResp] =
    removeRolePermissionsMock(roleId, data)
}
