package scommons.admin.client.role

import scommons.admin.client.api.role._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockRoleApi(
  getRoleByIdMock: Int => Future[RoleResp] = _ => ???,
  listRolesMock: () => Future[RoleListResp] = () => ???,
  createRoleMock: RoleData => Future[RoleResp] = _ => ???,
  updateRoleMock: RoleData => Future[RoleResp] = _ => ???
) extends RoleApi {

  def getRoleById(id: Int): Future[RoleResp] =
    getRoleByIdMock(id)

  def listRoles(): Future[RoleListResp] =
    listRolesMock()

  def createRole(data: RoleData): Future[RoleResp] =
    createRoleMock(data)

  def updateRole(data: RoleData): Future[RoleResp] =
    updateRoleMock(data)
}
