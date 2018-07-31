package services

import scommons.admin.domain.SystemEntity
import scommons.admin.domain.dao.SystemDao

import scala.concurrent.{ExecutionContext, Future}

class SystemService(systemDao: SystemDao)(implicit ec: ExecutionContext) {

  import systemDao.ctx

  def getSystemById(id: Int): Future[Option[SystemEntity]] = {
    systemDao.getById(id)
  }

  def getSystemByName(name: String): Future[Option[SystemEntity]] = {
    systemDao.getByName(name)
  }

  def listSystems(): Future[List[SystemEntity]] = {
    systemDao.list()
  }

  def createSystem(entity: SystemEntity): Future[SystemEntity] = {
    ctx.transaction { implicit ec =>
      systemDao.insert(entity).flatMap { id =>
        systemDao.getById(id).map(_.get)
      }
    }
  }
  
  def updateSystem(entity: SystemEntity): Future[Option[SystemEntity]] = {
    ctx.transaction { implicit ec =>
      systemDao.update(entity).flatMap {
        case false => Future.successful(None)
        case true => systemDao.getById(entity.id)
      }
    }
  }
}
