package scommons.admin.server.system.group

import play.api.libs.json.JsValue
import play.api.mvc._
import scommons.admin.client.api.system.group._
import scommons.play.controllers.BaseApiController

import scala.concurrent.ExecutionContext

class SystemGroupController(systemGroupApi: SystemGroupApi)
                           (implicit components: ControllerComponents, ec: ExecutionContext)
  extends BaseApiController(components) {

  def getById(id: Int): Action[AnyContent] = {
    apiNoBodyAction[SystemGroupResp] {
      systemGroupApi.getSystemGroupById(id)
    }
  }

  def list(): Action[AnyContent] = {
    apiNoBodyAction[SystemGroupListResp] {
      systemGroupApi.listSystemGroups()
    }
  }

  def create(): Action[JsValue] = {
    apiAction[SystemGroupData, SystemGroupResp] { data =>
      systemGroupApi.createSystemGroup(data)
    }
  }

  def update(): Action[JsValue] = {
    apiAction[SystemGroupData, SystemGroupResp] { data =>
      systemGroupApi.updateSystemGroup(data)
    }
  }
}
