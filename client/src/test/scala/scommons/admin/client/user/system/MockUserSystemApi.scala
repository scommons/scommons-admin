package scommons.admin.client.user.system

import scommons.admin.client.api.user.system._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockUserSystemApi(
  listUserSystemsMock: Int => Future[UserSystemResp] = _ => ???,
  addUserSystemsMock: (Int, UserSystemUpdateReq) => Future[UserSystemResp] = (_, _) => ???,
  removeUserSystemsMock: (Int, UserSystemUpdateReq) => Future[UserSystemResp] = (_, _) => ???
) extends UserSystemApi {

  def listUserSystems(userId: Int): Future[UserSystemResp] =
    listUserSystemsMock(userId)

  def addUserSystems(userId: Int, data: UserSystemUpdateReq): Future[UserSystemResp] =
    addUserSystemsMock(userId, data)

  def removeUserSystems(userId: Int, data: UserSystemUpdateReq): Future[UserSystemResp] =
    removeUserSystemsMock(userId, data)
}
