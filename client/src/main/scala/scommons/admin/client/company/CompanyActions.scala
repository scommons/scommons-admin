package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.company._
import scommons.admin.client.company.CompanyActions._
import scommons.api.ApiStatus.Ok
import scommons.react.redux.task.{FutureTask, TaskAction}

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

  case class CompanyCreateAction(task: FutureTask[CompanyResp]) extends TaskAction
  case class CompanyCreatedAction(data: CompanyData) extends Action
  case class CompanyCreateRequestAction(create: Boolean) extends Action

  case class CompanyListFetchAction(task: FutureTask[CompanyListResp],
                                    offset: Option[Int]) extends TaskAction

  case class CompanyListFetchedAction(dataList: List[CompanyData],
                                      totalCount: Option[Int]) extends Action

  case class CompanySelectedAction(id: Int) extends Action

  case class CompanyUpdateAction(task: FutureTask[CompanyResp]) extends TaskAction
  case class CompanyUpdatedAction(data: CompanyData) extends Action
  case class CompanyUpdateRequestAction(update: Boolean) extends Action
}
