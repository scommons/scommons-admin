package scommons.admin.server.system.user

import scommons.admin.domain.dao.SystemUserDao
import scommons.admin.domain.{SystemUser, User}

import scala.concurrent.{ExecutionContext, Future}

class SystemUserService(systemUserDao: SystemUserDao)(implicit ec: ExecutionContext) {

  def listSystemUsers(systemId: Int,
                      offset: Option[Int],
                      limit: Int,
                      symbols: Option[String]): Future[(List[(SystemUser, User)], Option[Int])] = {

    systemUserDao.list(systemId, offset, limit, symbols)
  }
}
