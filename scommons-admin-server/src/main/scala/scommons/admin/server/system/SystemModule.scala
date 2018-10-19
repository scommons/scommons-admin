package scommons.admin.server.system

import akka.actor.ActorSystem
import play.api.mvc.ControllerComponents
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.domain.dao.SystemDao

import scala.concurrent.ExecutionContext

trait SystemModule extends Module {

  private implicit lazy val systemComponents: ControllerComponents = inject[ControllerComponents]
  private implicit lazy val systemEc: ExecutionContext = inject[ActorSystem].dispatcher

  bind[SystemDao] to new SystemDao(
    inject[AdminDBContext]
  )

  bind[SystemService] to new SystemService(
    inject[SystemDao]
  )

  bind[SystemApiImpl] to new SystemApiImpl(
    inject[SystemService]
  )

  bind[SystemController] to new SystemController(
    inject[SystemApiImpl]
  )
}
