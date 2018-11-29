package scommons.admin.client.api.system.user

import scala.concurrent.Future

trait SystemUserApi {

  def listSystemUsers(systemId: Int,
                      offset: Option[Int],
                      limit: Option[Int],
                      symbols: Option[String]): Future[SystemUserListResp]
}
