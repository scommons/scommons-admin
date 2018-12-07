package scommons.admin.server.system.user

import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.system.user._
import scommons.admin.domain._
import scommons.admin.server.role.permission.{RolePermissionApiImpl, RolePermissionService}
import scommons.admin.server.system.SystemService
import scommons.admin.server.system.user.SystemUserApiImpl._

import scala.concurrent.{ExecutionContext, Future}

class SystemUserApiImpl(systemService: SystemService,
                        systemUserService: SystemUserService,
                        rolePermissionService: RolePermissionService
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

  def listSystemUserRoles(systemId: Int, userId: Int): Future[SystemUserRoleResp] = {
    systemService.getSystemById(systemId).flatMap {
      case None => Future.successful(SystemUserRoleResp(SystemNotFound))
      case Some(_) =>
        systemUserService.getSystemUser(systemId, userId).flatMap {
          case None => Future.successful(SystemUserRoleResp(SystemUserNotFound))
          case Some((su, user)) => getSystemUserRolesPermissions(su).map { case (roles, permissions) =>
            convertToSystemUserRoleResp(roles, permissions, su, user)
          }
        }
    }
  }

  def addSystemUserRoles(systemId: Int, userId: Int, data: SystemUserRoleUpdateReq): Future[SystemUserRoleResp] = {
    updateSystemUserRoles(systemId, userId, data, add = true)
  }

  def removeSystemUserRoles(systemId: Int, userId: Int, data: SystemUserRoleUpdateReq): Future[SystemUserRoleResp] = {
    updateSystemUserRoles(systemId, userId, data, add = false)
  }

  private def updateSystemUserRoles(systemId: Int,
                                    userId: Int,
                                    data: SystemUserRoleUpdateReq,
                                    add: Boolean): Future[SystemUserRoleResp] = {
    
    systemService.getSystemById(systemId).flatMap {
      case None => Future.successful(SystemUserRoleResp(SystemNotFound))
      case Some(_) =>
        systemUserService.getSystemUser(systemId, userId).flatMap {
          case None => Future.successful(SystemUserRoleResp(SystemUserNotFound))
          case Some((su, user)) => systemUserService.updateSystemUserRoles(
            su = su.copy(version = data.version),
            roleIds = data.roleIds,
            add = add
          ).flatMap {
            case None => Future.successful(SystemUserRoleResp(SystemUserAlreadyUpdated))
            case Some(updated) =>
              getSystemUserRolesPermissions(updated).map { case (roles, permissions) =>
                convertToSystemUserRoleResp(roles, permissions, updated, user)
              }
          }
        }
    }
  }

  private def getSystemUserRolesPermissions(su: SystemUser
                                           ): Future[(List[(Role, Boolean)], List[(Permission, Boolean)])] = {
    for {
      roles <- systemUserService.listSystemUserRoles(su)
      permissions <- rolePermissionService.listRolePermissions(
        systemId = su.systemId,
        roles = roles.collect { case (role, true) => role }.toSet
      )
    } yield (roles, permissions)
  }
}

object SystemUserApiImpl {

  def convertToSystemUserRoleResp(roles: List[(Role, Boolean)],
                                  permissions: List[(Permission, Boolean)],
                                  su: SystemUser,
                                  user: User): SystemUserRoleResp = {

    SystemUserRoleResp(SystemUserRoleRespData(
      roles = roles.map { case (r, isSelected) =>
        convertToSystemUserRoleData(r, isSelected)
      },
      permissions = permissions.map { case (p, isEnabled) =>
        RolePermissionApiImpl.convertToRolePermissionData(p, isEnabled)
      },
      systemUser = convertToSystemUserData(su, user)
    ))
  }

  def convertToSystemUserRoleData(r: Role, isSelected: Boolean): SystemUserRoleData = {
    SystemUserRoleData(
      id = r.id,
      title = r.title,
      isSelected = isSelected
    )
  }
  
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
