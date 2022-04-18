package scommons.admin.client.system

import scommons.admin.client.api.system.{SystemApi, SystemData}
import scommons.admin.client.system.SystemActions._
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockSystemActions(
  systemListFetchMock: Dispatch => SystemListFetchAction = _ => ???,
  systemCreateMock: (Dispatch, SystemData) => SystemCreateAction = (_, _) => ???,
  systemUpdateMock: (Dispatch, SystemData) => SystemUpdateAction = (_, _) => ???
) extends SystemActions {

  override protected def client: SystemApi = ???
  
  override def systemListFetch(dispatch: Dispatch): SystemListFetchAction =
    systemListFetchMock(dispatch)
    
  override def systemCreate(dispatch: Dispatch, data: SystemData): SystemCreateAction =
    systemCreateMock(dispatch, data)
    
  override def systemUpdate(dispatch: Dispatch, data: SystemData): SystemUpdateAction =
    systemUpdateMock(dispatch, data)
}
