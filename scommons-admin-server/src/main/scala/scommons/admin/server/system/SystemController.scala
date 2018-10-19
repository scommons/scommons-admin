package scommons.admin.server.system

import play.api.libs.json.JsValue
import play.api.mvc._
import scommons.admin.client.api.system._
import scommons.play.controllers.BaseApiController

import scala.concurrent.ExecutionContext

class SystemController(systemApi: SystemApi)
                      (implicit components: ControllerComponents, ec: ExecutionContext)
  extends BaseApiController(components) {

  def getById(id: Int): Action[AnyContent] = {
    apiNoBodyAction[SystemResp] {
      systemApi.getSystemById(id)
    }
  }

  def list(): Action[AnyContent] = {
    apiNoBodyAction[SystemListResp] {
      systemApi.listSystems()
    }
  }

  def create(): Action[JsValue] = {
    apiAction[SystemData, SystemResp] { data =>
      systemApi.createSystem(data)
    }
  }

  def update(): Action[JsValue] = {
    apiAction[SystemData, SystemResp] { data =>
      systemApi.updateSystem(data)
    }
  }
}
