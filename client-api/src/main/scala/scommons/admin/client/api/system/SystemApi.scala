package scommons.admin.client.api.system

import scala.concurrent.Future

trait SystemApi {

  def getSystemById(id: Int): Future[SystemResp]

  def listSystems(): Future[SystemListResp]

  def createSystem(data: SystemData): Future[SystemResp]
  
  def updateSystem(data: SystemData): Future[SystemResp]
}
