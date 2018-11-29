package scommons.admin.server.system.user

import akka.actor.ActorSystem
import play.api.mvc.ControllerComponents
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.domain.dao.SystemUserDao
import scommons.admin.server.system.SystemService

trait SystemUserModule extends Module {

  private implicit lazy val systemUserComponents = inject[ControllerComponents]
  private implicit lazy val systemUserEc = inject[ActorSystem].dispatcher

  bind[SystemUserDao] to new SystemUserDao(
    inject[AdminDBContext]
  )

  bind[SystemUserService] to new SystemUserService(
    inject[SystemUserDao]
  )

  bind[SystemUserApiImpl] to new SystemUserApiImpl(
    inject[SystemService],
    inject[SystemUserService]
  )

  bind[SystemUserController] to new SystemUserController(
    inject[SystemUserApiImpl]
  )
}
