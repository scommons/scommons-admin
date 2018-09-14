package scommons.admin.client.user

import scommons.admin.client.api.user.UserData
import scommons.admin.client.user.UserActions._

case class UserState(dataList: List[UserData] = Nil,
                     offset: Option[Int] = None,
                     totalCount: Option[Int] = None,
                     selectedId: Option[Int] = None,
                     showCreatePopup: Boolean = false,
                     showEditPopup: Boolean = false)

object UserStateReducer {

  def apply(state: Option[UserState], action: Any): UserState = {
    reduce(state.getOrElse(UserState()), action)
  }
  
  private def reduce(state: UserState, action: Any): UserState = action match {
    case a: UserCreateRequestAction => state.copy(showCreatePopup = a.create)
    case a: UserUpdateRequestAction => state.copy(showEditPopup = a.update)
    case a: UserSelectedAction => state.copy(selectedId = Some(a.id))
    case a: UserListFetchAction => state.copy(offset = a.offset)
    case UserListFetchedAction(dataList, totalCount) => state.copy(
      dataList = dataList,
      totalCount = totalCount.orElse(state.totalCount)
    )
    case UserCreatedAction(data) => state.copy(dataList = state.dataList :+ data.user)
    case UserUpdatedAction(data) => state.copy(dataList = state.dataList.map {
      case curr if curr.id == data.user.id => data.user
      case curr => curr
    })
    case _ => state
  }
}
