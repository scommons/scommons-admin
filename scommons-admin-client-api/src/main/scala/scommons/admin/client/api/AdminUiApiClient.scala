package scommons.admin.client.api

import scommons.admin.client.api.company._
import scommons.admin.client.api.system.group._
import scommons.api.http.ApiHttpClient

import scala.concurrent.Future

class AdminUiApiClient(client: ApiHttpClient)
  extends CompanyApi
    with SystemGroupApi {

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
  // systems/groups

  def getSystemGroupById(id: Int): Future[SystemGroupResp] = {
    client.execGet[SystemGroupResp](s"/systems/groups/$id")
  }

  def listSystemGroups(): Future[SystemGroupListResp] = {
    client.execGet[SystemGroupListResp]("/systems/groups")
  }

  def createSystemGroup(data: SystemGroupData): Future[SystemGroupResp] = {
    client.execPost[SystemGroupData, SystemGroupResp]("/systems/groups", data)
  }

  def updateSystemGroup(data: SystemGroupData): Future[SystemGroupResp] = {
    client.execPut[SystemGroupData, SystemGroupResp]("/systems/groups", data)
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // users

}
