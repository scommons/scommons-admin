package scommons.admin.client.role.action

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.role._
import scommons.api.ApiStatus.Ok
import scommons.client.task.FutureTask

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
