package apis.ui

import apis.ui.RoleApiImpl._
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.role._
import scommons.admin.domain.Role
import services.RoleService

import scala.concurrent.{ExecutionContext, Future}

class RoleApiImpl(service: RoleService)(implicit ec: ExecutionContext)
  extends RoleApi {

  def getRoleById(id: Int): Future[RoleResp] = {
    service.getRoleById(id).map {
      case None => RoleResp(RoleNotFound)
      case Some(entity) => RoleResp(convertToRoleData(entity))
    }
  }

  def listRoles(): Future[RoleListResp] = {
    service.listRoles().map { list =>
      RoleListResp(list.map(convertToRoleData))
    }
  }

  def createRole(data: RoleData): Future[RoleResp] = {
    validateRoleData(false, data, { entity =>
      service.createRole(entity).map { repo =>
        RoleResp(convertToRoleData(repo))
      }
    })
  }

  def updateRole(data: RoleData): Future[RoleResp] = {
    validateRoleData(true, data, { entity =>
      service.updateRole(entity).map {
        case None => RoleResp(RoleAlreadyUpdated)
        case Some(updated) => RoleResp(convertToRoleData(updated))
      }
    })
  }

  private def validateRoleData(update: Boolean,
                               data: RoleData,
                               onSuccess: Role => Future[RoleResp]
                              ): Future[RoleResp] = {

    val entity = convertToRole(data)
    if (entity.title.isEmpty) {
      throw new IllegalArgumentException("title is blank")
    }

    def getById(data: RoleData) = data.id match {
      case Some(id) if update => service.getRoleById(id)
      case _ => Future.successful(None)
    }

    def getByName(current: Option[Role],
                  entity: Role): Future[Option[Role]] = current match {
      
      case Some(curr) if curr.title == entity.title => Future.successful(None)
      case _ => service.getRoleByTitle(entity.systemId, entity.title)
    }

    getById(data).flatMap { current =>
      if (current.isEmpty && update) Future.successful(RoleResp(RoleNotFound))
      else {
        Future.sequence(List(
          getByName(current, entity)
        )).flatMap {
          case List(Some(_)) => Future.successful(RoleResp(RoleAlreadyExists))
          case List(None) => current match {
            case None => onSuccess(entity)
            case Some(curr) => onSuccess(entity.copy(
              //DON'T UPDATE READ-ONLY FIELDS !!!
              systemId = curr.systemId,
              bitIndex = curr.bitIndex
            ))
          }
        }
      }
    }
  }
}

object RoleApiImpl {

  def convertToRoleData(c: Role): RoleData = RoleData(
    id = Some(c.id),
    systemId = c.systemId,
    title = c.title,
    updatedAt = Some(c.updatedAt),
    createdAt = Some(c.createdAt),
    version = Some(c.version)
  )

  def convertToRole(data: RoleData): Role = Role(
    id = data.id.getOrElse(-1),
    systemId = data.systemId,
    bitIndex = 0,
    title = data.title.trim,
    updatedBy = 1, //TODO: use current userId (from request)
    version = data.version.getOrElse(-1)
  )
}
