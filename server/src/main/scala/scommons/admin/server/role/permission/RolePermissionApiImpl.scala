package scommons.admin.server.role.permission

import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.role.permission._
import scommons.admin.domain.{Permission, Role}
import scommons.admin.server.role.permission.RolePermissionApiImpl._
import scommons.admin.server.role.{RoleApiImpl, RoleService}

import scala.concurrent.{ExecutionContext, Future}

class RolePermissionApiImpl(roleService: RoleService,
                            rolePermissionService: RolePermissionService)(implicit ec: ExecutionContext)
  extends RolePermissionApi {

  def listRolePermissions(roleId: Int): Future[RolePermissionResp] = {
    roleService.getRoleById(roleId).flatMap {
      case None => Future.successful(RolePermissionResp(RoleNotFound))
      case Some(role) =>
        rolePermissionService.listRolePermissions(role.systemId, Set(role)).map { permissions =>
          convertToRolePermissionResp(permissions, role)
        }
    }
  }

  def addRolePermissions(roleId: Int, data: RolePermissionUpdateReq): Future[RolePermissionResp] = {
    roleService.getRoleById(roleId).flatMap {
      case None => Future.successful(RolePermissionResp(RoleNotFound))
      case Some(role) =>
        (for {
          permissions <- rolePermissionService.addRolePermissions(
            role.copy(version = data.version),
            data.permissionIds
          )
          updatedRole <- roleService.getRoleById(roleId)
        } yield {
          permissions.flatMap(px => updatedRole.map(r => (px, r)))
        }).map {
          case None => RolePermissionResp(RoleAlreadyUpdated)
          case Some((permissions, updatedRole)) =>
            convertToRolePermissionResp(permissions, updatedRole)
        }
    }
  }

  def removeRolePermissions(roleId: Int, data: RolePermissionUpdateReq): Future[RolePermissionResp] = {
    roleService.getRoleById(roleId).flatMap {
      case None => Future.successful(RolePermissionResp(RoleNotFound))
      case Some(role) =>
        (for {
          permissions <- rolePermissionService.removeRolePermissions(
            role.copy(version = data.version),
            data.permissionIds
          )
          updatedRole <- roleService.getRoleById(roleId)
        } yield {
          permissions.flatMap(px => updatedRole.map(r => (px, r)))
        }).map {
          case None => RolePermissionResp(RoleAlreadyUpdated)
          case Some((permissions, updatedRole)) =>
            convertToRolePermissionResp(permissions, updatedRole)
        }
    }
  }
}

object RolePermissionApiImpl {

  def convertToRolePermissionResp(permissions: List[(Permission, Boolean)],
                                  role: Role): RolePermissionResp = {

    RolePermissionResp(RolePermissionRespData(
      permissions = permissions.map { case (p, isEnabled) =>
        convertToRolePermissionData(p, isEnabled)
      },
      role = RoleApiImpl.convertToRoleData(role)
    ))
  }
  
  def convertToRolePermissionData(p: Permission, isEnabled: Boolean): RolePermissionData = {
    RolePermissionData(
      id = p.id,
      parentId = p.parentId,
      isNode = p.isNode,
      title = p.title,
      isEnabled = isEnabled
    )
  }
}
