package scommons.admin.server.role.permission

import play.api.libs.json.JsValue
import play.api.mvc._
import scommons.admin.client.api.role.permission._
import scommons.play.controllers.BaseApiController

import scala.concurrent.ExecutionContext

class RolePermissionController(rolePermissionApi: RolePermissionApi)
                              (implicit components: ControllerComponents, ec: ExecutionContext)
  extends BaseApiController(components) {

  def list(id: Int): Action[AnyContent] = {
    apiNoBodyAction[RolePermissionResp] {
      rolePermissionApi.listRolePermissions(id)
    }
  }

  def add(id: Int): Action[JsValue] = {
    apiAction[RolePermissionUpdateReq, RolePermissionResp] { data =>
      rolePermissionApi.addRolePermissions(id, data)
    }
  }

  def remove(id: Int): Action[JsValue] = {
    apiAction[RolePermissionUpdateReq, RolePermissionResp] { data =>
      rolePermissionApi.removeRolePermissions(id, data)
    }
  }
}
