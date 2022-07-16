package scommons.admin.server.system

import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.system._
import scommons.admin.domain.SystemEntity
import scommons.admin.server.system.SystemApiImpl._

import scala.concurrent.{ExecutionContext, Future}

class SystemApiImpl(service: SystemService)(implicit ec: ExecutionContext)
  extends SystemApi {

  def getSystemById(id: Int): Future[SystemResp] = {
    service.getSystemById(id).map {
      case None => SystemResp(SystemNotFound)
      case Some(entity) => SystemResp(convertToSystemData(entity))
    }
  }

  def listSystems(): Future[SystemListResp] = {
    service.listSystems().map { list =>
      SystemListResp(list.map(convertToSystemData))
    }
  }

  def createSystem(data: SystemData): Future[SystemResp] = {
    validateSystemData(false, data, { entity =>
      service.createSystem(entity).map { repo =>
        SystemResp(convertToSystemData(repo))
      }
    })
  }

  def updateSystem(data: SystemData): Future[SystemResp] = {
    validateSystemData(true, data, { entity =>
      service.updateSystem(entity).map {
        case None => SystemResp(SystemAlreadyUpdated)
        case Some(updated) => SystemResp(convertToSystemData(updated))
      }
    })
  }

  private def validateSystemData(update: Boolean,
                                 data: SystemData,
                                 onSuccess: SystemEntity => Future[SystemResp]
                                ): Future[SystemResp] = {

    val entity = convertToSystem(data)
    if (entity.name.isEmpty) {
      throw new IllegalArgumentException("name is blank")
    }

    def getById(data: SystemData) = data.id match {
      case Some(id) if update => service.getSystemById(id)
      case _ => Future.successful(None)
    }

    def getByName(current: Option[SystemEntity],
                  entity: SystemEntity): Future[Option[SystemEntity]] = current match {
      
      case Some(curr) if curr.name == entity.name => Future.successful(None)
      case _ => service.getSystemByName(entity.name)
    }

    getById(data).flatMap { current =>
      if (current.isEmpty && update) Future.successful(SystemResp(SystemNotFound))
      else {
        getByName(current, entity).flatMap {
          case Some(_) => Future.successful(SystemResp(SystemAlreadyExists))
          case None => current match {
            case None => onSuccess(entity)
            case Some(curr) => onSuccess(entity.copy(
              //DON'T UPDATE READ-ONLY FIELDS !!!
              parentId = curr.parentId
            ))
          }
        }
      }
    }
  }
}

object SystemApiImpl {

  def convertToSystemData(c: SystemEntity): SystemData = SystemData(
    id = Some(c.id),
    name = c.name,
    password = c.password,
    title = c.title,
    url = c.url,
    parentId = c.parentId,
    updatedAt = Some(c.updatedAt),
    createdAt = Some(c.createdAt),
    version = Some(c.version)
  )

  def convertToSystem(data: SystemData): SystemEntity = SystemEntity(
    id = data.id.getOrElse(-1),
    name = data.name.trim,
    password = data.password,
    title = data.title.trim,
    url = data.url.trim,
    parentId = data.parentId,
    updatedBy = 1, //TODO: use current userId (from request)
    version = data.version.getOrElse(-1)
  )
}
