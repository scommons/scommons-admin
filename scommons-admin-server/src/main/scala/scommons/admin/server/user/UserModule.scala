package scommons.admin.server.user

import akka.actor.ActorSystem
import play.api.mvc.ControllerComponents
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.domain.dao.{UserDao, UserProfileDao}
import scommons.admin.server.company.CompanyService

trait UserModule extends Module {

  private implicit lazy val userComponents = inject[ControllerComponents]
  private implicit lazy val userEc = inject[ActorSystem].dispatcher

  bind[UserDao] to new UserDao(
    inject[AdminDBContext]
  )

  bind[UserProfileDao] to new UserProfileDao(
    inject[AdminDBContext]
  )

  bind[UserService] to new UserService(
    inject[UserDao],
    inject[UserProfileDao]
  )

  bind[UserApiImpl] to new UserApiImpl(
    inject[UserService],
    inject[CompanyService]
  )

  bind[UserController] to new UserController(
    inject[UserApiImpl]
  )
}
