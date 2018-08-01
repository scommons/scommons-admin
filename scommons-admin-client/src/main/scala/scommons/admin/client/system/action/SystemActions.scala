package scommons.admin.client.system.action

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.system._
import scommons.api.ApiStatus.Ok
import scommons.client.task.FutureTask

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait SystemActions {

  protected def client: SystemApi

  def systemListFetch(dispatch: Dispatch): SystemListFetchAction = {
    val future = client.listSystems().andThen {
      case Success(SystemListResp(Ok, Some(dataList))) =>
        dispatch(SystemListFetchedAction(dataList))
    }
    
    SystemListFetchAction(FutureTask("Fetching Applications", future))
  }

  def systemCreate(dispatch: Dispatch, data: SystemData): SystemCreateAction = {
    val future = client.createSystem(data).andThen {
      case Success(SystemResp(Ok, Some(respData))) =>
        dispatch(SystemCreatedAction(respData))
    }

    SystemCreateAction(FutureTask("Creating Application", future))
  }

  def systemUpdate(dispatch: Dispatch, data: SystemData): SystemUpdateAction = {
    val future = client.updateSystem(data).andThen {
      case Success(SystemResp(Ok, Some(respData))) =>
        dispatch(SystemUpdatedAction(respData))
    }

    SystemUpdateAction(FutureTask("Updating Application", future))
  }
}
