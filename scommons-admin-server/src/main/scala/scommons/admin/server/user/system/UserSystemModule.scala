package scommons.admin.server.user.system

import akka.actor.ActorSystem
import play.api.mvc.ControllerComponents
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.domain.dao.{SystemDao, SystemUserDao, UserDao}
import scommons.admin.server.user.UserService

import scala.concurrent.ExecutionContext

trait UserSystemModule extends Module {

  private implicit lazy val userSystemComponents: ControllerComponents = inject[ControllerComponents]
  private implicit lazy val userSystemEc: ExecutionContext = inject[ActorSystem].dispatcher

  bind[SystemUserDao] to new SystemUserDao(
    inject[AdminDBContext]
  )

  bind[UserSystemService] to new UserSystemService(
    inject[UserDao],
    inject[SystemDao],
    inject[SystemUserDao]
  )

  bind[UserSystemApiImpl] to new UserSystemApiImpl(
    inject[UserService],
    inject[UserSystemService]
  )

  bind[UserSystemController] to new UserSystemController(
    inject[UserSystemApiImpl]
  )
}
