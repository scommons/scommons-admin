package scommons.admin.client.role.permission

import scommons.admin.client.api.role.permission._
import scommons.admin.client.role.RoleActions.RoleUpdatedAction
import scommons.admin.client.role.permission.RolePermissionActions._
import scommons.api.ApiStatus.Ok
import scommons.react.redux._
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait RolePermissionActions {

  protected def client: RolePermissionApi

  def rolePermissionsFetch(dispatch: Dispatch, roleId: Int): RolePermissionFetchAction = {
    val future = client.listRolePermissions(roleId).andThen {
      case Success(RolePermissionResp(Ok, Some(respData))) =>
        dispatch(RoleUpdatedAction(respData.role))
        dispatch(RolePermissionFetchedAction(respData))
    }

    RolePermissionFetchAction(FutureTask("Fetching Role Permissions", future))
  }

  def rolePermissionsAdd(dispatch: Dispatch, roleId: Int, data: RolePermissionUpdateReq): RolePermissionAddAction = {
    val future = client.addRolePermissions(roleId, data).andThen {
      case Success(RolePermissionResp(Ok, Some(respData))) =>
        dispatch(RoleUpdatedAction(respData.role))
        dispatch(RolePermissionAddedAction(respData))
    }

    RolePermissionAddAction(FutureTask("Adding Role Permissions", future))
  }

  def rolePermissionsRemove(dispatch: Dispatch, roleId: Int, data: RolePermissionUpdateReq): RolePermissionRemoveAction = {
    val future = client.removeRolePermissions(roleId, data).andThen {
      case Success(RolePermissionResp(Ok, Some(respData))) =>
        dispatch(RoleUpdatedAction(respData.role))
        dispatch(RolePermissionRemovedAction(respData))
    }

    RolePermissionRemoveAction(FutureTask("Removing Role Permissions", future))
  }
}

object RolePermissionActions {

  case class RolePermissionFetchAction(task: FutureTask[RolePermissionResp]) extends TaskAction
  case class RolePermissionFetchedAction(data: RolePermissionRespData) extends Action

  case class RolePermissionAddAction(task: FutureTask[RolePermissionResp]) extends TaskAction
  case class RolePermissionAddedAction(data: RolePermissionRespData) extends Action

  case class RolePermissionRemoveAction(task: FutureTask[RolePermissionResp]) extends TaskAction
  case class RolePermissionRemovedAction(data: RolePermissionRespData) extends Action
}
