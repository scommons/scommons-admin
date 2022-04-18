package scommons.admin.client.company

import scommons.admin.client.api.company._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockCompanyApi(
  getCompanyByIdMock: Int => Future[CompanyResp] = _ => ???,
  listCompaniesMock: (Option[Int], Option[Int], Option[String]) => Future[CompanyListResp] = (_, _, _) => ???,
  createCompanyMock: CompanyData => Future[CompanyResp] = _ => ???,
  updateCompanyMock: CompanyData => Future[CompanyResp] = _ => ???
) extends CompanyApi {

  def getCompanyById(id: Int): Future[CompanyResp] =
    getCompanyByIdMock(id)

  def listCompanies(offset: Option[Int], limit: Option[Int], symbols: Option[String]): Future[CompanyListResp] =
    listCompaniesMock(offset, limit, symbols)

  def createCompany(data: CompanyData): Future[CompanyResp] =
    createCompanyMock(data)

  def updateCompany(data: CompanyData): Future[CompanyResp] =
    updateCompanyMock(data)
}
