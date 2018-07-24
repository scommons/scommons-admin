package scommons.admin.client.api.company

import scala.concurrent.Future

trait CompanyApi {

  def getCompanyById(id: Int): Future[CompanyResp]
  
  def listCompanies(offset: Option[Int],
                    limit: Option[Int],
                    symbols: Option[String]): Future[CompanyListResp]

  def createCompany(data: CompanyData): Future[CompanyResp]
  
  def updateCompany(data: CompanyData): Future[CompanyResp]
}
