package modules

import akka.actor.ActorSystem
import apis.ui.RoleApiImpl
import controllers.ui.RoleController
import play.api.mvc.ControllerComponents
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.domain.dao.RoleDao
import services.RoleService

import scala.concurrent.ExecutionContext

trait RoleModule extends Module {

  private implicit lazy val roleComponents: ControllerComponents = inject[ControllerComponents]
  private implicit lazy val roleEc: ExecutionContext = inject[ActorSystem].dispatcher

  bind[RoleDao] to new RoleDao(
    inject[AdminDBContext]
  )

  bind[RoleService] to new RoleService(
    inject[RoleDao]
  )

  bind[RoleApiImpl] to new RoleApiImpl(
    inject[RoleService]
  )

  bind[RoleController] to new RoleController(
    inject[RoleApiImpl]
  )
}
