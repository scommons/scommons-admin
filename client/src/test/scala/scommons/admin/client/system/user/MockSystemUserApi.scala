package scommons.admin.client.system.user

import scommons.admin.client.api.system.user._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockSystemUserApi(
  listSystemUsersMock: (Int, Option[Int], Option[Int], Option[String]) => Future[SystemUserListResp] = (_, _, _, _) => ???,
  listSystemUserRolesMock: (Int, Int) => Future[SystemUserRoleResp] = (_, _) => ???,
  addSystemUserRolesMock: (Int, Int, SystemUserRoleUpdateReq) => Future[SystemUserRoleResp] = (_, _, _) => ???,
  removeSystemUserRolesMock: (Int, Int, SystemUserRoleUpdateReq) => Future[SystemUserRoleResp] = (_, _, _) => ???
) extends SystemUserApi {

  def listSystemUsers(systemId: Int, offset: Option[Int], limit: Option[Int], symbols: Option[String]): Future[SystemUserListResp] =
    listSystemUsersMock(systemId, offset, limit, symbols)

  def listSystemUserRoles(systemId: Int, userId: Int): Future[SystemUserRoleResp] =
    listSystemUserRolesMock(systemId, userId)

  def addSystemUserRoles(systemId: Int, userId: Int, data: SystemUserRoleUpdateReq): Future[SystemUserRoleResp] =
    addSystemUserRolesMock(systemId, userId, data)

  def removeSystemUserRoles(systemId: Int, userId: Int, data: SystemUserRoleUpdateReq): Future[SystemUserRoleResp] =
    removeSystemUserRolesMock(systemId, userId, data)
}
