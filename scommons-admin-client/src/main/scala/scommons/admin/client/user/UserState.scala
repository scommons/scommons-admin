package scommons.admin.client.user

import scommons.admin.client.api.user.{UserData, UserDetailsData}
import scommons.admin.client.user.UserActions._

case class UserState(dataList: List[UserData] = Nil,
                     offset: Option[Int] = None,
                     totalCount: Option[Int] = None,
                     selected: Option[UserDetailsData] = None,
                     showCreatePopup: Boolean = false,
                     showEditPopup: Boolean = false)

object UserStateReducer {

  def apply(state: Option[UserState], action: Any): UserState = {
    reduce(state.getOrElse(UserState()), action)
  }
  
  private def reduce(state: UserState, action: Any): UserState = action match {
    case a: UserCreateRequestAction => state.copy(showCreatePopup = a.create)
    case a: UserUpdateRequestAction => state.copy(showEditPopup = a.update)
    case UserFetchedAction(data) => state.copy(
      dataList = state.dataList.map {
        case curr if curr.id == data.user.id => data.user
        case curr => curr
      },
      selected = Some(data)
    )
    case a: UserListFetchAction => state.copy(offset = a.offset)
    case UserListFetchedAction(dataList, totalCount) => state.copy(
      dataList = dataList,
      totalCount = totalCount.orElse(state.totalCount),
      selected = None
    )
    case UserCreatedAction(data) => state.copy(
      dataList = state.dataList :+ data.user,
      selected = Some(data),
      showCreatePopup = false
    )
    case UserUpdatedAction(data) => state.copy(
      dataList = state.dataList.map {
        case curr if curr.id == data.user.id => data.user
        case curr => curr
      },
      selected = Some(data),
      showEditPopup = false
    )
    case _ => state
  }
}
