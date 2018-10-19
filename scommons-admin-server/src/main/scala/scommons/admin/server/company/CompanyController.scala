package scommons.admin.server.company

import play.api.libs.json.JsValue
import play.api.mvc._
import scommons.admin.client.api.company._
import scommons.play.controllers.BaseApiController

import scala.concurrent.ExecutionContext

class CompanyController(companyApi: CompanyApi)
                       (implicit components: ControllerComponents, ec: ExecutionContext)
  extends BaseApiController(components) {

  def getById(id: Int): Action[AnyContent] = {
    apiNoBodyAction[CompanyResp] {
      companyApi.getCompanyById(id)
    }
  }

  def list(offset: Option[Int],
           limit: Option[Int],
           symbols: Option[String]): Action[AnyContent] = {
    
    apiNoBodyAction[CompanyListResp] {
      companyApi.listCompanies(offset, limit, symbols)
    }
  }

  def create(): Action[JsValue] = {
    apiAction[CompanyData, CompanyResp] { data =>
      companyApi.createCompany(data)
    }
  }

  def update(): Action[JsValue] = {
    apiAction[CompanyData, CompanyResp] { data =>
      companyApi.updateCompany(data)
    }
  }
}
