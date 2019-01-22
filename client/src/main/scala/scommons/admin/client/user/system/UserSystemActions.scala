package scommons.admin.client.user.system

import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.user.system._
import scommons.admin.client.user.UserActions.UserUpdatedAction
import scommons.admin.client.user.system.UserSystemActions._
import scommons.api.ApiStatus.Ok
import scommons.client.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait UserSystemActions {

  protected def client: UserSystemApi

  def userSystemsFetch(dispatch: Dispatch, userId: Int): UserSystemFetchAction = {
    val future = client.listUserSystems(userId).andThen {
      case Success(UserSystemResp(Ok, Some(respData))) =>
        dispatch(UserUpdatedAction(respData.user))
        dispatch(UserSystemFetchedAction(respData))
    }

    UserSystemFetchAction(FutureTask("Fetching User Applications", future))
  }

  def userSystemsAdd(dispatch: Dispatch, userId: Int, data: UserSystemUpdateReq): UserSystemAddAction = {
    val future = client.addUserSystems(userId, data).andThen {
      case Success(UserSystemResp(Ok, Some(respData))) =>
        dispatch(UserUpdatedAction(respData.user))
        dispatch(UserSystemAddedAction(respData))
    }

    UserSystemAddAction(FutureTask("Adding User Applications", future))
  }

  def userSystemsRemove(dispatch: Dispatch, userId: Int, data: UserSystemUpdateReq): UserSystemRemoveAction = {
    val future = client.removeUserSystems(userId, data).andThen {
      case Success(UserSystemResp(Ok, Some(respData))) =>
        dispatch(UserUpdatedAction(respData.user))
        dispatch(UserSystemRemovedAction(respData))
    }

    UserSystemRemoveAction(FutureTask("Removing User Applications", future))
  }
}

object UserSystemActions {

  case class UserSystemFetchAction(task: FutureTask[UserSystemResp]) extends TaskAction
  case class UserSystemFetchedAction(data: UserSystemRespData) extends Action

  case class UserSystemAddAction(task: FutureTask[UserSystemResp]) extends TaskAction
  case class UserSystemAddedAction(data: UserSystemRespData) extends Action

  case class UserSystemRemoveAction(task: FutureTask[UserSystemResp]) extends TaskAction
  case class UserSystemRemovedAction(data: UserSystemRespData) extends Action
}
