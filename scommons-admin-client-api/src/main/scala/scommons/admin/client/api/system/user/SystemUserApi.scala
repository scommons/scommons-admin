package scommons.admin.client.api.system.user

import scala.concurrent.Future

trait SystemUserApi {

  def listSystemUsers(systemId: Int,
                      offset: Option[Int],
                      limit: Option[Int],
                      symbols: Option[String]): Future[SystemUserListResp]

  def listSystemUserRoles(systemId: Int, userId: Int): Future[SystemUserRoleResp]

  def addSystemUserRoles(systemId: Int, userId: Int, data: SystemUserRoleUpdateReq): Future[SystemUserRoleResp]

  def removeSystemUserRoles(systemId: Int, userId: Int, data: SystemUserRoleUpdateReq): Future[SystemUserRoleResp]
}
