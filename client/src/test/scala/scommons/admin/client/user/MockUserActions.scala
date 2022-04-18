package scommons.admin.client.user

import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions._
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockUserActions(
  userListFetchMock: (Dispatch, Option[Int], Option[String]) => UserListFetchAction = (_, _, _) => ???,
  userFetchMock: (Dispatch, Int) => UserFetchAction = (_, _) => ???,
  userCreateMock: (Dispatch, UserDetailsData) => UserCreateAction = (_, _) => ???,
  userUpdateMock: (Dispatch, UserDetailsData) => UserUpdateAction = (_, _) => ???
) extends UserActions {
  
  override protected def client: UserApi = ???

  override def userListFetch(dispatch: Dispatch,
                             offset: Option[Int],
                             symbols: Option[String]): UserListFetchAction = {
    userListFetchMock(dispatch, offset, symbols)
  }
  
  override def userFetch(dispatch: Dispatch, id: Int): UserFetchAction =
    userFetchMock(dispatch, id)
    
  override def userCreate(dispatch: Dispatch, data: UserDetailsData): UserCreateAction =
    userCreateMock(dispatch, data)
    
  override def userUpdate(dispatch: Dispatch, data: UserDetailsData): UserUpdateAction =
    userUpdateMock(dispatch, data)
}
