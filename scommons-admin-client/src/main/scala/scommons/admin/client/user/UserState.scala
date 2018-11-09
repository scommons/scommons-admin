package scommons.admin.client.user

import scommons.admin.client.AdminRouteController.buildUsersPath
import scommons.admin.client.api.user.{UserData, UserDetailsData}
import scommons.admin.client.user.UserActions._
import scommons.client.util.BrowsePath

case class UserState(usersPath: BrowsePath = buildUsersPath(None),
                     dataList: List[UserData] = Nil,
                     offset: Option[Int] = None,
                     totalCount: Option[Int] = None,
                     userDetails: Option[UserDetailsData] = None,
                     showCreatePopup: Boolean = false,
                     showEditPopup: Boolean = false)

object UserStateReducer {

  def apply(state: Option[UserState], action: Any): UserState = {
    reduce(state.getOrElse(UserState()), action)
  }
  
  private def reduce(state: UserState, action: Any): UserState = action match {
    case a: UsersPathChangedAction => state.copy(usersPath = a.path)
    case a: UserCreateRequestAction => state.copy(showCreatePopup = a.create)
    case a: UserUpdateRequestAction => state.copy(showEditPopup = a.update)
    case UserFetchedAction(data) => state.copy(
      usersPath = buildUsersPath(data.user.id),
      dataList = state.dataList.map {
        case curr if curr.id == data.user.id => data.user
        case curr => curr
      },
      userDetails = Some(data)
    )
    case a: UserListFetchAction => state.copy(offset = a.offset)
    case UserListFetchedAction(dataList, totalCount) => state.copy(
      dataList = dataList,
      totalCount = totalCount.orElse(state.totalCount)
    )
    case UserCreatedAction(data) => state.copy(
      dataList = state.dataList :+ data.user,
      userDetails = Some(data),
      showCreatePopup = false
    )
    case UserUpdatedAction(data) => state.copy(
      dataList = state.dataList.map {
        case curr if curr.id == data.user.id => data.user
        case curr => curr
      },
      userDetails = Some(data),
      showEditPopup = false
    )
    case _ => state
  }
}
