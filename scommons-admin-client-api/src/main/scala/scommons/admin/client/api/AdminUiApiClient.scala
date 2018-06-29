package scommons.admin.client.api

import scommons.admin.client.api.company._
import scommons.api.http.ApiHttpClient

import scala.concurrent.Future

class AdminUiApiClient(client: ApiHttpClient)
  extends CompanyApi {

  ////////////////////////////////////////////////////////////////////////////////////////
  // companies

  def getCompanyById(id: Int): Future[CompanyResp] = {
    client.execGet[CompanyResp](s"/companies/$id")
  }
  
  def listCompanies(offset: Option[Int] = None,
                    limit: Option[Int] = None,
                    symbols: Option[String] = None): Future[CompanyListResp] = {

    client.execGet[CompanyListResp]("/companies", params = ApiHttpClient.queryParams(
      "offset" -> offset,
      "limit" -> limit,
      "symbols" -> symbols
    ))
  }

  def createCompany(data: CompanyData): Future[CompanyResp] = {
    client.execPost[CompanyData, CompanyResp]("/companies", data)
  }

  def updateCompany(data: CompanyData): Future[CompanyResp] = {
    client.execPut[CompanyData, CompanyResp]("/companies", data)
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // users

}
