package scommons.admin.client.company

import scommons.admin.client.api.company._
import scommons.admin.client.company.CompanyActions._
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockCompanyActions(
  companyListFetchMock: (Dispatch, Option[Int], Option[String]) => CompanyListFetchAction = (_, _, _) => ???,
  companyCreateMock: (Dispatch, String) => CompanyCreateAction = (_, _) => ???,
  companyUpdateMock: (Dispatch, CompanyData) => CompanyUpdateAction = (_, _) => ???
) extends CompanyActions {

  override protected def client: CompanyApi = ???

  override def companyListFetch(dispatch: Dispatch,
                                offset: Option[Int],
                                symbols: Option[String]): CompanyListFetchAction = {

    companyListFetchMock(dispatch, offset, symbols)
  }

  override def companyCreate(dispatch: Dispatch, name: String): CompanyCreateAction =
    companyCreateMock(dispatch, name)

  override def companyUpdate(dispatch: Dispatch, data: CompanyData): CompanyUpdateAction =
    companyUpdateMock(dispatch, data)
}
