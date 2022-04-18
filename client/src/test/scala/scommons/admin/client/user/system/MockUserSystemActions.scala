package scommons.admin.client.user.system

import scommons.admin.client.api.user.system._
import scommons.admin.client.user.system.UserSystemActions._
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockUserSystemActions(
  userSystemsFetchMock: (Dispatch, Int) => UserSystemFetchAction = (_, _) => ???,
  userSystemsAddMock: (Dispatch, Int, UserSystemUpdateReq) => UserSystemAddAction = (_, _, _) => ???,
  userSystemsRemoveMock: (Dispatch, Int, UserSystemUpdateReq) => UserSystemRemoveAction = (_, _, _) => ???
) extends UserSystemActions {

  override protected def client: UserSystemApi = ???
  
  override def userSystemsFetch(dispatch: Dispatch, userId: Int): UserSystemFetchAction =
    userSystemsFetchMock(dispatch, userId)
    
  override def userSystemsAdd(dispatch: Dispatch, userId: Int, data: UserSystemUpdateReq): UserSystemAddAction =
    userSystemsAddMock(dispatch, userId, data)
    
  override def userSystemsRemove(dispatch: Dispatch, userId: Int, data: UserSystemUpdateReq): UserSystemRemoveAction =
    userSystemsRemoveMock(dispatch, userId, data)
}
