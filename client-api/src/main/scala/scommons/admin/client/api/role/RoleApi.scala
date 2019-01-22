package scommons.admin.client.api.role

import scala.concurrent.Future

trait RoleApi {

  def getRoleById(id: Int): Future[RoleResp]

  def listRoles(): Future[RoleListResp]

  def createRole(data: RoleData): Future[RoleResp]
  
  def updateRole(data: RoleData): Future[RoleResp]
}
