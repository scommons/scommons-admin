package scommons.admin.client.api.role.permission

import scala.concurrent.Future

trait RolePermissionApi {

  def listRolePermissions(roleId: Int): Future[RolePermissionResp]

  def addRolePermissions(roleId: Int, data: RolePermissionUpdateReq): Future[RolePermissionResp]
  
  def removeRolePermissions(roleId: Int, data: RolePermissionUpdateReq): Future[RolePermissionResp]
}
