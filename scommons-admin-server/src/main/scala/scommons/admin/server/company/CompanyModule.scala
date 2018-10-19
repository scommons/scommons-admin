package scommons.admin.server.company

import akka.actor.ActorSystem
import play.api.mvc.ControllerComponents
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.domain.dao.CompanyDao

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
