package scommons.admin.client.role

import scommons.admin.client.api.role._
import scommons.admin.client.role.RoleActions._
import scommons.api.ApiStatus.Ok
import scommons.react.redux._
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait RoleActions {

  protected def client: RoleApi

  def roleListFetch(dispatch: Dispatch): RoleListFetchAction = {
    val future = client.listRoles().andThen {
      case Success(RoleListResp(Ok, Some(dataList))) =>
        dispatch(RoleListFetchedAction(dataList))
    }
    
    RoleListFetchAction(FutureTask("Fetching Roles", future))
  }

  def roleCreate(dispatch: Dispatch, data: RoleData): RoleCreateAction = {
    val future = client.createRole(data).andThen {
      case Success(RoleResp(Ok, Some(respData))) =>
        dispatch(RoleCreatedAction(respData))
    }

    RoleCreateAction(FutureTask("Creating Role", future))
  }

  def roleUpdate(dispatch: Dispatch, data: RoleData): RoleUpdateAction = {
    val future = client.updateRole(data).andThen {
      case Success(RoleResp(Ok, Some(respData))) =>
        dispatch(RoleUpdatedAction(respData))
    }

    RoleUpdateAction(FutureTask("Updating Role", future))
  }
}

object RoleActions {

  case class RoleCreateAction(task: FutureTask[RoleResp]) extends TaskAction
  case class RoleCreatedAction(data: RoleData) extends Action
  case class RoleCreateRequestAction(create: Boolean) extends Action

  case class RoleListFetchAction(task: FutureTask[RoleListResp]) extends TaskAction
  case class RoleListFetchedAction(dataList: List[RoleData]) extends Action

  case class RoleUpdateAction(task: FutureTask[RoleResp]) extends TaskAction
  case class RoleUpdatedAction(data: RoleData) extends Action
  case class RoleUpdateRequestAction(update: Boolean) extends Action
}
