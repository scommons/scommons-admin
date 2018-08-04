package apis.ui

import apis.ui.SystemGroupApiImpl._
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.system.group._
import scommons.admin.domain.SystemGroup
import services.SystemGroupService

import scala.concurrent.{ExecutionContext, Future}

class SystemGroupApiImpl(service: SystemGroupService)(implicit ec: ExecutionContext)
  extends SystemGroupApi {

  def getSystemGroupById(id: Int): Future[SystemGroupResp] = {
    service.getSystemGroupById(id).map {
      case None => SystemGroupResp(SystemGroupNotFound)
      case Some(entity) => SystemGroupResp(convertToSystemGroupData(entity))
    }
  }

  def listSystemGroups(): Future[SystemGroupListResp] = {
    service.listSystemGroups().map { list =>
      SystemGroupListResp(list.map(convertToSystemGroupData))
    }
  }

  def createSystemGroup(data: SystemGroupData): Future[SystemGroupResp] = {
    validateSystemGroupData(false, data, { entity =>
      service.createSystemGroup(entity).map { repo =>
        SystemGroupResp(convertToSystemGroupData(repo))
      }
    })
  }

  def updateSystemGroup(data: SystemGroupData): Future[SystemGroupResp] = {
    validateSystemGroupData(true, data, { entity =>
      service.updateSystemGroup(entity).map {
        case None => SystemGroupResp(SystemGroupAlreadyUpdated)
        case Some(updated) => SystemGroupResp(convertToSystemGroupData(updated))
      }
    })
  }

  private def validateSystemGroupData(update: Boolean,
                                      data: SystemGroupData,
                                      onSuccess: SystemGroup => Future[SystemGroupResp]
                                     ): Future[SystemGroupResp] = {

    val entity = convertToSystemGroup(data)
    if (entity.name.isEmpty) {
      throw new IllegalArgumentException("name is blank")
    }

    def getById(data: SystemGroupData) = data.id match {
      case Some(id) if update => service.getSystemGroupById(id)
      case _ => Future.successful(None)
    }

    def getByName(current: Option[SystemGroup],
                  entity: SystemGroup): Future[Option[SystemGroup]] = current match {
      
      case Some(curr) if curr.name == entity.name => Future.successful(None)
      case _ => service.getSystemGroupByName(entity.name)
    }

    getById(data).flatMap { current =>
      if (current.isEmpty && update) Future.successful(SystemGroupResp(SystemGroupNotFound))
      else {
        Future.sequence(List(
          getByName(current, entity)
        )).flatMap {
          case List(Some(_)) => Future.successful(SystemGroupResp(SystemGroupAlreadyExists))
          case List(None) => onSuccess(entity)
        }
      }
    }
  }
}

object SystemGroupApiImpl {

  def convertToSystemGroupData(c: SystemGroup): SystemGroupData = SystemGroupData(
    id = Some(c.id),
    name = c.name,
    updatedAt = Some(c.updatedAt),
    createdAt = Some(c.createdAt),
    version = Some(c.version)
  )

  def convertToSystemGroup(data: SystemGroupData): SystemGroup = SystemGroup(
    id = data.id.getOrElse(-1),
    name = data.name.trim,
    updatedBy = 1, //TODO: use current userId (from request)
    version = data.version.getOrElse(-1)
  )
}
