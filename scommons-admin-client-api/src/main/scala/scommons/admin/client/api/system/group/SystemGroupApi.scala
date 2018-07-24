package scommons.admin.client.api.system.group

import scala.concurrent.Future

trait SystemGroupApi {

  def getSystemGroupById(id: Int): Future[SystemGroupResp]

  def listSystemGroups(): Future[SystemGroupListResp]

  def createSystemGroup(data: SystemGroupData): Future[SystemGroupResp]
  
  def updateSystemGroup(data: SystemGroupData): Future[SystemGroupResp]
}
