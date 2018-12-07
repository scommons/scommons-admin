package scommons.admin.server.system.user

import scommons.admin.domain.dao.{RoleDao, SystemUserDao}
import scommons.admin.domain.{Role, SystemUser, User}

import scala.collection.immutable.BitSet
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class SystemUserService(systemUserDao: SystemUserDao,
                        roleDao: RoleDao)(implicit ec: ExecutionContext) {

  import systemUserDao.ctx
  
  def getSystemUser(systemId: Int, userId: Int): Future[Option[(SystemUser, User)]] = {
    systemUserDao.getSystemUser(systemId, userId)
  }
  
  def listSystemUsers(systemId: Int,
                      offset: Option[Int],
                      limit: Int,
                      symbols: Option[String]): Future[(List[(SystemUser, User)], Option[Int])] = {

    systemUserDao.list(systemId, offset, limit, symbols)
  }

  def listSystemUserRoles(su: SystemUser): Future[List[(Role, Boolean)]] = {
    val bits = BitSet.fromBitMask(Array(su.roles))
    
    roleDao.listBySystemId(su.systemId).map { roles =>
      roles.map { role =>
        (role, bits.contains(role.bitIndex))
      }
    }
  }

  def updateSystemUserRoles(su: SystemUser, roleIds: Set[Int], add: Boolean): Future[Option[SystemUser]] = {
    ctx.transaction { implicit ec: ExecutionContext =>
      for {
        systemRoles <- roleDao.listBySystemId(su.systemId)
        newRoles = {
          val roles = systemRoles.filter(r => roleIds.contains(r.id))
          val bits = mutable.BitSet.fromBitMask(Array(su.roles))
          for (r <- roles) {
            if (add) bits += r.bitIndex
            else bits -= r.bitIndex
          }
          bits.toBitMask.headOption.getOrElse(0L)
        }
        updated <- systemUserDao.update(su.copy(roles = newRoles))
        res <-
          if (!updated) Future.successful(None)
          else systemUserDao.getById(su.systemId, su.userId)
      } yield res
    }
  }
}
