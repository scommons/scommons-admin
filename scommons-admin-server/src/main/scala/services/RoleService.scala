package services

import scommons.admin.domain.Role
import scommons.admin.domain.dao.RoleDao

import scala.concurrent.{ExecutionContext, Future}

class RoleService(roleDao: RoleDao)(implicit ec: ExecutionContext) {

  import roleDao.ctx

  def getRoleById(id: Int): Future[Option[Role]] = {
    roleDao.getById(id)
  }

  def getRoleByTitle(systemId: Int, title: String): Future[Option[Role]] = {
    roleDao.getByTitle(systemId, title)
  }
  
  def listRoles(): Future[List[Role]] = {
    roleDao.list()
  }

  def createRole(entity: Role): Future[Role] = {
    ctx.transaction { implicit ec =>
      roleDao.getMaxBitIndex(entity.systemId).flatMap { maxBitIndex =>
        val roleWithBitIndex = entity.copy(bitIndex = maxBitIndex.map { bitIndex =>
          if (bitIndex == RoleService.bitIndexLimit) {
            throw new IllegalStateException(s"Reached role bit_index limit($bitIndex)")
          }
          
          bitIndex + 1
        }.getOrElse(0))
        
        roleDao.insert(roleWithBitIndex).flatMap { id =>
          roleDao.getById(id).map(_.get)
        }
      }
    }
  }
  
  def updateRole(entity: Role): Future[Option[Role]] = {
    ctx.transaction { implicit ec =>
      roleDao.update(entity).flatMap {
        case false => Future.successful(None)
        case true => roleDao.getById(entity.id)
      }
    }
  }
}

object RoleService {
  
  private val bitIndexLimit = 63
}
