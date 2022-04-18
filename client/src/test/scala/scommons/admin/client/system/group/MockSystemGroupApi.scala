package scommons.admin.client.system.group

import scommons.admin.client.api.system.group._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockSystemGroupApi(
  getSystemGroupByIdMock: Int => Future[SystemGroupResp] = _ => ???,
  listSystemGroupsMock: () => Future[SystemGroupListResp] = () => ???,
  createSystemGroupMock: SystemGroupData => Future[SystemGroupResp] = _ => ???,
  updateSystemGroupMock: SystemGroupData => Future[SystemGroupResp] = _ => ???
) extends SystemGroupApi {

  def getSystemGroupById(id: Int): Future[SystemGroupResp] =
    getSystemGroupByIdMock(id)

  def listSystemGroups(): Future[SystemGroupListResp] =
    listSystemGroupsMock()

  def createSystemGroup(data: SystemGroupData): Future[SystemGroupResp] =
    createSystemGroupMock(data)

  def updateSystemGroup(data: SystemGroupData): Future[SystemGroupResp] =
    updateSystemGroupMock(data)
}
