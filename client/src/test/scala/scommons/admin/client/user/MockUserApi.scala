package scommons.admin.client.user

import scommons.admin.client.api.user._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockUserApi(
  getUserByIdMock: Int => Future[UserDetailsResp] = _ => ???,
  listUsersMock: (Option[Int], Option[Int], Option[String]) => Future[UserListResp] = (_, _, _) => ???,
  createUserMock: UserDetailsData => Future[UserDetailsResp] = _ => ???,
  updateUserMock: UserDetailsData => Future[UserDetailsResp] = _ => ???
) extends UserApi {

  def getUserById(id: Int): Future[UserDetailsResp] =
    getUserByIdMock(id)

  def listUsers(offset: Option[Int], limit: Option[Int], symbols: Option[String]): Future[UserListResp] =
    listUsersMock(offset, limit, symbols)

  def createUser(data: UserDetailsData): Future[UserDetailsResp] =
    createUserMock(data)

  def updateUser(data: UserDetailsData): Future[UserDetailsResp] =
    updateUserMock(data)
}
