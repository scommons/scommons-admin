package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.system.user._
import scommons.admin.client.system.user.SystemUserActions._
import scommons.api.ApiStatus.Ok
import scommons.client.task.{FutureTask, TaskAction}

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

    SystemUserListFetchAction(FutureTask("Fetching Application Users", future), systemId, offset)
  }
}

object SystemUserActions {

  val listLimit = 10

  case class SystemUserListFetchAction(task: FutureTask[SystemUserListResp],
                                       systemId: Int,
                                       offset: Option[Int]) extends TaskAction

  case class SystemUserListFetchedAction(dataList: List[SystemUserData],
                                         totalCount: Option[Int]) extends Action

}
