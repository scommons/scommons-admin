package scommons.admin.client.system.group

import scommons.admin.client.api.system.group.{SystemGroupApi, SystemGroupData}
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockSystemGroupActions(
  systemGroupListFetchMock: Dispatch => SystemGroupListFetchAction = _ => ???,
  systemGroupCreateMock: (Dispatch, String) => SystemGroupCreateAction = (_, _) => ???,
  systemGroupUpdateMock: (Dispatch, SystemGroupData) => SystemGroupUpdateAction = (_, _) => ???
) extends SystemGroupActions {

  override protected def client: SystemGroupApi = ???
  
  override def systemGroupListFetch(dispatch: Dispatch): SystemGroupListFetchAction =
    systemGroupListFetchMock(dispatch)
    
  override def systemGroupCreate(dispatch: Dispatch, name: String): SystemGroupCreateAction =
    systemGroupCreateMock(dispatch, name)
    
  override def systemGroupUpdate(dispatch: Dispatch, data: SystemGroupData): SystemGroupUpdateAction =
    systemGroupUpdateMock(dispatch, data)
}
