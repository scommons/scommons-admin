package scommons.admin.client.role

import scommons.admin.client.api.role.{RoleApi, RoleData}
import scommons.admin.client.role.RoleActions._
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockRoleActions(
  roleListFetchMock: Dispatch => RoleListFetchAction = _ => ???,
  roleCreateMock: (Dispatch, RoleData) => RoleCreateAction = (_, _) => ???,
  roleUpdateMock: (Dispatch, RoleData) => RoleUpdateAction = (_, _) => ???
) extends RoleActions {

  override protected def client: RoleApi = ???

  override def roleListFetch(dispatch: Dispatch): RoleListFetchAction =
    roleListFetchMock(dispatch)

  override def roleCreate(dispatch: Dispatch, data: RoleData): RoleCreateAction =
    roleCreateMock(dispatch, data)

  override def roleUpdate(dispatch: Dispatch, data: RoleData): RoleUpdateAction =
    roleUpdateMock(dispatch, data)
}
