package scommons.admin.client.api.user.system

import scala.concurrent.Future

trait UserSystemApi {

  def listUserSystems(userId: Int): Future[UserSystemResp]

  def addUserSystems(userId: Int, data: UserSystemUpdateReq): Future[UserSystemResp]
  
  def removeUserSystems(userId: Int, data: UserSystemUpdateReq): Future[UserSystemResp]
}
