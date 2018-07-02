package scommons.admin.client.company.action

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.company._
import scommons.api.ApiStatus.Ok
import scommons.client.task.FutureTask

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait CompanyActions {

  protected def client: CompanyApi

  def companyListFetch(dispatch: Dispatch,
                       offset: Option[Int],
                       symbols: Option[String]): CompanyListFetchAction = {

    val future = client.listCompanies(offset, Some(CompanyActions.listLimit), symbols).andThen {
      case Success(CompanyListResp(Ok, Some(dataList), totalCount)) =>
        dispatch(CompanyListFetchedAction(dataList, totalCount))
    }
    
    CompanyListFetchAction(FutureTask("Fetching Companies", future), offset)
  }

  def companyCreate(dispatch: Dispatch, name: String): CompanyCreateAction = {
    val future = client.createCompany(CompanyData(None, name)).andThen {
      case Success(CompanyResp(Ok, Some(data))) =>
        dispatch(CompanyCreatedAction(data))
    }

    CompanyCreateAction(FutureTask("Creating Company", future))
  }

  def companyUpdate(dispatch: Dispatch, data: CompanyData): CompanyUpdateAction = {
    val future = client.updateCompany(data).andThen {
      case Success(CompanyResp(Ok, Some(respData))) =>
        dispatch(CompanyUpdatedAction(respData))
    }

    CompanyUpdateAction(FutureTask("Updating Company", future))
  }
}

object CompanyActions {

  val listLimit = 10
}
