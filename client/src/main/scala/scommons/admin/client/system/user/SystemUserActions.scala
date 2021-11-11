package scommons.admin.client.system.user

import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.system.user._
import scommons.admin.client.system.user.SystemUserActions._
import scommons.api.ApiStatus.Ok
import scommons.react.redux._
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait SystemUserActions {

  protected def client: SystemUserApi

  def systemUserListFetch(dispatch: Dispatch,
                          systemId: Int,
                          offset: Option[Int],
                          symbols: Option[String]): SystemUserListFetchAction = {

    val future = client.listSystemUsers(systemId, offset, Some(listLimit), symbols).andThen {
      case Success(SystemUserListResp(Ok, Some(dataList), totalCount)) =>
        dispatch(SystemUserListFetchedAction(dataList, totalCount))
    }

    SystemUserListFetchAction(FutureTask("Fetching Application Users", future), offset)
  }

  def systemUserRolesFetch(dispatch: Dispatch, systemId: Int, userId: Int): SystemUserRoleFetchAction = {
    val future = client.listSystemUserRoles(systemId, userId).map {
      case SystemUserRoleResp(SystemUserNotFound, _) => SystemUserRoleResp(Ok, None)
      case resp => resp
    }.andThen {
      case Success(SystemUserRoleResp(Ok, respData)) =>
        dispatch(SystemUserRoleFetchedAction(respData))
    }

    SystemUserRoleFetchAction(FutureTask("Fetching User Permissions", future))
  }

  def systemUserRolesAdd(dispatch: Dispatch,
                         systemId: Int,
                         userId: Int,
                         data: SystemUserRoleUpdateReq): SystemUserRoleAddAction = {
    
    val future = client.addSystemUserRoles(systemId, userId, data).andThen {
      case Success(SystemUserRoleResp(Ok, Some(respData))) =>
        dispatch(SystemUserRoleAddedAction(respData))
    }

    SystemUserRoleAddAction(FutureTask("Adding User Permissions", future))
  }

  def systemUserRolesRemove(dispatch: Dispatch,
                            systemId: Int,
                            userId: Int,
                            data: SystemUserRoleUpdateReq): SystemUserRoleRemoveAction = {
    
    val future = client.removeSystemUserRoles(systemId, userId, data).andThen {
      case Success(SystemUserRoleResp(Ok, Some(respData))) =>
        dispatch(SystemUserRoleRemovedAction(respData))
    }

    SystemUserRoleRemoveAction(FutureTask("Removing User Permissions", future))
  }
}

object SystemUserActions {

  val listLimit = 10

  case class SystemUserParamsChangedAction(params: SystemUserParams) extends Action
  
  case class SystemUserListFetchAction(task: FutureTask[SystemUserListResp],
                                       offset: Option[Int]) extends TaskAction

  case class SystemUserListFetchedAction(dataList: List[SystemUserData],
                                         totalCount: Option[Int]) extends Action

  case class SystemUserRoleFetchAction(task: FutureTask[SystemUserRoleResp]) extends TaskAction
  case class SystemUserRoleFetchedAction(data: Option[SystemUserRoleRespData]) extends Action

  case class SystemUserRoleAddAction(task: FutureTask[SystemUserRoleResp]) extends TaskAction
  case class SystemUserRoleAddedAction(data: SystemUserRoleRespData) extends Action

  case class SystemUserRoleRemoveAction(task: FutureTask[SystemUserRoleResp]) extends TaskAction
  case class SystemUserRoleRemovedAction(data: SystemUserRoleRespData) extends Action
}
