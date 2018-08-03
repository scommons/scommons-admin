package controllers.ui

import play.api.libs.json.JsValue
import play.api.mvc._
import scommons.admin.client.api.role._
import scommons.play.controllers.BaseApiController

import scala.concurrent.ExecutionContext

class RoleController(roleApi: RoleApi)(implicit components: ControllerComponents, ec: ExecutionContext)
  extends BaseApiController(components) {

  def getById(id: Int): Action[AnyContent] = {
    apiNoBodyAction[RoleResp] {
      roleApi.getRoleById(id)
    }
  }

  def list(): Action[AnyContent] = {
    apiNoBodyAction[RoleListResp] {
      roleApi.listRoles()
    }
  }

  def create(): Action[JsValue] = {
    apiAction[RoleData, RoleResp] { data =>
      roleApi.createRole(data)
    }
  }

  def update(): Action[JsValue] = {
    apiAction[RoleData, RoleResp] { data =>
      roleApi.updateRole(data)
    }
  }
}
