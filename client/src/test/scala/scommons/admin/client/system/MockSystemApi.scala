package scommons.admin.client.system

import scommons.admin.client.api.system._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockSystemApi(
  getSystemByIdMock: Int => Future[SystemResp] = _ => ???,
  listSystemsMock: () => Future[SystemListResp] = () => ???,
  createSystemMock: SystemData => Future[SystemResp] = _ => ???,
  updateSystemMock: SystemData => Future[SystemResp] = _ => ???
) extends SystemApi {

  def getSystemById(id: Int): Future[SystemResp] =
    getSystemByIdMock(id)

  def listSystems(): Future[SystemListResp] =
    listSystemsMock()

  def createSystem(data: SystemData): Future[SystemResp] =
    createSystemMock(data)

  def updateSystem(data: SystemData): Future[SystemResp] =
    updateSystemMock(data)
}
