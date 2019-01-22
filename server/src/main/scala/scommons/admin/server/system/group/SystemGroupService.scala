package scommons.admin.server.system.group

import scommons.admin.domain.SystemGroup
import scommons.admin.domain.dao.SystemGroupDao

import scala.concurrent.{ExecutionContext, Future}

class SystemGroupService(systemGroupDao: SystemGroupDao)(implicit ec: ExecutionContext) {

  import systemGroupDao.ctx

  def getSystemGroupById(id: Int): Future[Option[SystemGroup]] = {
    systemGroupDao.getById(id)
  }

  def getSystemGroupByName(name: String): Future[Option[SystemGroup]] = {
    systemGroupDao.getByName(name)
  }

  def listSystemGroups(): Future[List[SystemGroup]] = {
    systemGroupDao.list()
  }

  def createSystemGroup(entity: SystemGroup): Future[SystemGroup] = {
    ctx.transaction { implicit ec =>
      systemGroupDao.insert(entity).flatMap { id =>
        systemGroupDao.getById(id).map(_.get)
      }
    }
  }
  
  def updateSystemGroup(entity: SystemGroup): Future[Option[SystemGroup]] = {
    ctx.transaction { implicit ec =>
      systemGroupDao.update(entity).flatMap {
        case false => Future.successful(None)
        case true => systemGroupDao.getById(entity.id)
      }
    }
  }
}
