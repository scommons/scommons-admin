package scommons.admin.client.system.user

import scommons.admin.client.api.system.user.SystemUserData
import scommons.admin.client.system.user.SystemUserActions._

case class SystemUserState(params: SystemUserParams = SystemUserParams(),
                           dataList: List[SystemUserData] = Nil,
                           offset: Option[Int] = None,
                           totalCount: Option[Int] = None)

object SystemUserStateReducer {

  def apply(state: Option[SystemUserState], action: Any): SystemUserState = {
    reduce(state.getOrElse(SystemUserState()), action)
  }
  
  private def reduce(state: SystemUserState, action: Any): SystemUserState = action match {
    case SystemUserParamsChangedAction(params) => state.copy(params = params)
    case SystemUserListFetchAction(_, offset) => state.copy(offset = offset)
    case SystemUserListFetchedAction(dataList, totalCount) => state.copy(
      dataList = dataList,
      totalCount = totalCount.orElse(state.totalCount)
    )
    case _ => state
  }
}