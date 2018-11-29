package scommons.admin.server.system.user

import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.system.user._
import scommons.admin.domain._
import scommons.admin.server.system.SystemService
import scommons.admin.server.system.user.SystemUserApiImpl._

import scala.concurrent.{ExecutionContext, Future}

class SystemUserApiImpl(systemService: SystemService,
                        systemUserService: SystemUserService
                       )(implicit ec: ExecutionContext) extends SystemUserApi {

  private val defaultLimit = 10

  def listSystemUsers(systemId: Int,
                      offset: Option[Int],
                      limit: Option[Int],
                      symbols: Option[String]): Future[SystemUserListResp] = {

    systemService.getSystemById(systemId).flatMap {
      case None => Future.successful(SystemUserListResp(SystemNotFound))
      case Some(_) =>
        systemUserService.listSystemUsers(systemId, offset, limit.getOrElse(defaultLimit), symbols).map {
          case (list, totalCount) =>
            SystemUserListResp(list.map { case (su, user) =>
              convertToSystemUserData(su, user)
            }, totalCount)
        }
    }
  }
}

object SystemUserApiImpl {

  def convertToSystemUserData(su: SystemUser, user: User): SystemUserData = {
    SystemUserData(
      userId = su.userId,
      login = user.login,
      lastLoginDate = user.lastLoginDate,
      updatedAt = su.updatedAt,
      createdAt = su.createdAt,
      version = su.version
    )
  }
}
