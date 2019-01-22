package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.system.group._
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.api.ApiStatus.Ok
import scommons.client.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait SystemGroupActions {

  protected def client: SystemGroupApi

  def systemGroupListFetch(dispatch: Dispatch): SystemGroupListFetchAction = {
    val future = client.listSystemGroups().andThen {
      case Success(SystemGroupListResp(Ok, Some(dataList))) =>
        dispatch(SystemGroupListFetchedAction(dataList))
    }
    
    SystemGroupListFetchAction(FutureTask("Fetching Environments", future))
  }

  def systemGroupCreate(dispatch: Dispatch, name: String): SystemGroupCreateAction = {
    val future = client.createSystemGroup(SystemGroupData(None, name)).andThen {
      case Success(SystemGroupResp(Ok, Some(data))) =>
        dispatch(SystemGroupCreatedAction(data))
    }

    SystemGroupCreateAction(FutureTask("Creating Environment", future))
  }

  def systemGroupUpdate(dispatch: Dispatch, data: SystemGroupData): SystemGroupUpdateAction = {
    val future = client.updateSystemGroup(data).andThen {
      case Success(SystemGroupResp(Ok, Some(respData))) =>
        dispatch(SystemGroupUpdatedAction(respData))
    }

    SystemGroupUpdateAction(FutureTask("Updating Environment", future))
  }
}

object SystemGroupActions {

  case class SystemGroupCreateAction(task: FutureTask[SystemGroupResp]) extends TaskAction
  case class SystemGroupCreatedAction(data: SystemGroupData) extends Action
  case class SystemGroupCreateRequestAction(create: Boolean) extends Action

  case class SystemGroupListFetchAction(task: FutureTask[SystemGroupListResp]) extends TaskAction
  case class SystemGroupListFetchedAction(dataList: List[SystemGroupData]) extends Action

  case class SystemGroupUpdateAction(task: FutureTask[SystemGroupResp]) extends TaskAction
  case class SystemGroupUpdatedAction(data: SystemGroupData) extends Action
  case class SystemGroupUpdateRequestAction(update: Boolean) extends Action
}
