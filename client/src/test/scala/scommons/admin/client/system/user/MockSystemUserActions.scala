package scommons.admin.client.system.user

import scommons.admin.client.api.system.user.{SystemUserApi, SystemUserRoleUpdateReq}
import scommons.admin.client.system.user.SystemUserActions._
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockSystemUserActions(
  systemUserListFetchMock: (Dispatch, Int, Option[Int], Option[String]) => SystemUserListFetchAction = (_, _, _, _) => ???,
  systemUserRolesFetchMock: (Dispatch, Int, Int) => SystemUserRoleFetchAction = (_, _, _) => ???,
  systemUserRolesAddMock: (Dispatch, Int, Int, SystemUserRoleUpdateReq) => SystemUserRoleAddAction = (_, _, _, _) => ???,
  systemUserRolesRemoveMock: (Dispatch, Int, Int, SystemUserRoleUpdateReq) => SystemUserRoleRemoveAction = (_, _, _, _) => ???
) extends SystemUserActions {

  override protected def client: SystemUserApi = ???
  
  override def systemUserListFetch(dispatch: Dispatch,
                                   systemId: Int,
                                   offset: Option[Int],
                                   symbols: Option[String]): SystemUserListFetchAction = {

    systemUserListFetchMock(dispatch, systemId, offset, symbols)
  }
  
  override def systemUserRolesFetch(dispatch: Dispatch, systemId: Int, userId: Int): SystemUserRoleFetchAction =
    systemUserRolesFetchMock(dispatch, systemId, userId)
    
  override def systemUserRolesAdd(dispatch: Dispatch,
                                  systemId: Int,
                                  userId: Int,
                                  data: SystemUserRoleUpdateReq): SystemUserRoleAddAction = {

    systemUserRolesAddMock(dispatch, systemId, userId, data)
  }

  override def systemUserRolesRemove(dispatch: Dispatch,
                                     systemId: Int,
                                     userId: Int,
                                     data: SystemUserRoleUpdateReq): SystemUserRoleRemoveAction = {

    systemUserRolesRemoveMock(dispatch, systemId, userId, data)
  }
}
