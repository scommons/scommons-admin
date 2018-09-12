package scommons.admin.client.api.user

import scala.concurrent.Future

trait UserApi {

  def getUserById(id: Int): Future[UserDetailsResp]

  def listUsers(offset: Option[Int],
                limit: Option[Int],
                symbols: Option[String]): Future[UserListResp]

  def createUser(data: UserDetailsData): Future[UserDetailsResp]
  
  def updateUser(data: UserDetailsData): Future[UserDetailsResp]
}
