package scommons.admin.server.system.group

import akka.actor.ActorSystem
import play.api.mvc.ControllerComponents
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.domain.dao.SystemGroupDao

import scala.concurrent.ExecutionContext

trait SystemGroupModule extends Module {

  private implicit lazy val systemGroupComponents: ControllerComponents = inject[ControllerComponents]
  private implicit lazy val systemGroupEc: ExecutionContext = inject[ActorSystem].dispatcher

  bind[SystemGroupDao] to new SystemGroupDao(
    inject[AdminDBContext]
  )

  bind[SystemGroupService] to new SystemGroupService(
    inject[SystemGroupDao]
  )

  bind[SystemGroupApiImpl] to new SystemGroupApiImpl(
    inject[SystemGroupService]
  )

  bind[SystemGroupController] to new SystemGroupController(
    inject[SystemGroupApiImpl]
  )
}
