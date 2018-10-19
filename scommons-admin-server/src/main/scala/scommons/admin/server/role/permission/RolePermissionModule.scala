package scommons.admin.server.role.permission

import akka.actor.ActorSystem
import play.api.mvc.ControllerComponents
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.domain.dao.{PermissionDao, RoleDao, RolePermissionDao}
import scommons.admin.server.role.RoleService

import scala.concurrent.ExecutionContext

trait RolePermissionModule extends Module {

  private implicit lazy val rolePermissionComponents: ControllerComponents = inject[ControllerComponents]
  private implicit lazy val rolePermissionEc: ExecutionContext = inject[ActorSystem].dispatcher

  bind[PermissionDao] to new PermissionDao(
    inject[AdminDBContext]
  )

  bind[RolePermissionDao] to new RolePermissionDao(
    inject[AdminDBContext]
  )

  bind[RolePermissionService] to new RolePermissionService(
    inject[RoleDao],
    inject[PermissionDao],
    inject[RolePermissionDao]
  )

  bind[RolePermissionApiImpl] to new RolePermissionApiImpl(
    inject[RoleService],
    inject[RolePermissionService]
  )

  bind[RolePermissionController] to new RolePermissionController(
    inject[RolePermissionApiImpl]
  )
}
