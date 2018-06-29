package modules

import akka.actor.ActorSystem
import apis.ui.CompanyApiImpl
import controllers.ui.CompanyController
import play.api.mvc.ControllerComponents
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.domain.dao.CompanyDao
import services.CompanyService

trait CompanyModule extends Module {

  private implicit lazy val companyComponents = inject[ControllerComponents]
  private implicit lazy val companyEc = inject[ActorSystem].dispatcher

  bind[CompanyDao] to new CompanyDao(
    inject[AdminDBContext]
  )

  bind[CompanyService] to new CompanyService(
    inject[CompanyDao]
  )

  bind[CompanyApiImpl] to new CompanyApiImpl(
    inject[CompanyService]
  )

  bind[CompanyController] to new CompanyController(
    inject[CompanyApiImpl]
  )
}
