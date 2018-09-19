package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions._
import scommons.api.ApiStatus.Ok
import scommons.client.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait UserActions {

  protected def client: UserApi

  def userListFetch(dispatch: Dispatch,
                    offset: Option[Int],
                    symbols: Option[String]): UserListFetchAction = {

    val future = client.listUsers(offset, Some(UserActions.listLimit), symbols).andThen {
      case Success(UserListResp(Ok, Some(dataList), totalCount)) =>
        dispatch(UserListFetchedAction(dataList, totalCount))
    }
    
    UserListFetchAction(FutureTask("Fetching Users", future), offset)
  }

  def userFetch(dispatch: Dispatch, id: Int): UserFetchAction = {
    val future = client.getUserById(id).andThen {
      case Success(UserDetailsResp(Ok, Some(respData))) =>
        dispatch(UserFetchedAction(respData))
    }

    UserFetchAction(FutureTask("Fetching User", future))
  }

  def userCreate(dispatch: Dispatch, data: UserDetailsData): UserCreateAction = {
    val future = client.createUser(data).andThen {
      case Success(UserDetailsResp(Ok, Some(respData))) =>
        dispatch(UserCreatedAction(respData))
    }

    UserCreateAction(FutureTask("Creating User", future))
  }

  def userUpdate(dispatch: Dispatch, data: UserDetailsData): UserUpdateAction = {
    val future = client.updateUser(data).andThen {
      case Success(UserDetailsResp(Ok, Some(respData))) =>
        dispatch(UserUpdatedAction(respData))
    }

    UserUpdateAction(FutureTask("Updating User", future))
  }
}

object UserActions {

  val listLimit = 10

  case class UserCreateAction(task: FutureTask[UserDetailsResp]) extends TaskAction
  case class UserCreatedAction(data: UserDetailsData) extends Action
  case class UserCreateRequestAction(create: Boolean) extends Action

  case class UserListFetchAction(task: FutureTask[UserListResp],
                                 offset: Option[Int]) extends TaskAction

  case class UserListFetchedAction(dataList: List[UserData],
                                   totalCount: Option[Int]) extends Action

  case class UserFetchAction(task: FutureTask[UserDetailsResp]) extends TaskAction
  case class UserFetchedAction(data: UserDetailsData) extends Action

  case class UserUpdateAction(task: FutureTask[UserDetailsResp]) extends TaskAction
  case class UserUpdatedAction(data: UserDetailsData) extends Action
  case class UserUpdateRequestAction(update: Boolean) extends Action
}
