package scommons.admin.server.user.system

import play.api.libs.json.JsValue
import play.api.mvc._
import scommons.admin.client.api.user.system._
import scommons.play.controllers.BaseApiController

import scala.concurrent.ExecutionContext

class UserSystemController(userSystemApi: UserSystemApi)
                          (implicit components: ControllerComponents, ec: ExecutionContext)
  extends BaseApiController(components) {

  def list(id: Int): Action[AnyContent] = {
    apiNoBodyAction[UserSystemResp] {
      userSystemApi.listUserSystems(id)
    }
  }

  def add(id: Int): Action[JsValue] = {
    apiAction[UserSystemUpdateReq, UserSystemResp] { data =>
      userSystemApi.addUserSystems(id, data)
    }
  }

  def remove(id: Int): Action[JsValue] = {
    apiAction[UserSystemUpdateReq, UserSystemResp] { data =>
      userSystemApi.removeUserSystems(id, data)
    }
  }
}
