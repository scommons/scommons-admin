package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.system._
import scommons.admin.client.system.SystemActions._
import scommons.api.ApiStatus.Ok
import scommons.client.task.{FutureTask, TaskAction}

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

object SystemActions {

  case class SystemCreateAction(task: FutureTask[SystemResp]) extends TaskAction
  case class SystemCreatedAction(data: SystemData) extends Action
  case class SystemCreateRequestAction(create: Boolean) extends Action

  case class SystemListFetchAction(task: FutureTask[SystemListResp]) extends TaskAction
  case class SystemListFetchedAction(dataList: List[SystemData]) extends Action

  case class SystemUpdateAction(task: FutureTask[SystemResp]) extends TaskAction
  case class SystemUpdatedAction(data: SystemData) extends Action
  case class SystemUpdateRequestAction(update: Boolean) extends Action
}
