package scommons.admin.server.system.user

import play.api.libs.json.JsValue
import play.api.mvc._
import scommons.admin.client.api.system.user._
import scommons.play.controllers.BaseApiController

import scala.concurrent.ExecutionContext

class SystemUserController(systemUserApi: SystemUserApi)
                          (implicit components: ControllerComponents, ec: ExecutionContext)
  extends BaseApiController(components) {

  def listUsers(id: Int,
                offset: Option[Int],
                limit: Option[Int],
                symbols: Option[String]): Action[AnyContent] = {
    
    apiNoBodyAction[SystemUserListResp] {
      systemUserApi.listSystemUsers(id, offset, limit, symbols)
    }
  }

  def listRoles(systemId: Int, userId: Int): Action[AnyContent] = {
    apiNoBodyAction[SystemUserRoleResp] {
      systemUserApi.listSystemUserRoles(systemId, userId)
    }
  }

  def addRoles(systemId: Int, userId: Int): Action[JsValue] = {
    apiAction[SystemUserRoleUpdateReq, SystemUserRoleResp] { data =>
      systemUserApi.addSystemUserRoles(systemId, userId, data)
    }
  }

  def removeRoles(systemId: Int, userId: Int): Action[JsValue] = {
    apiAction[SystemUserRoleUpdateReq, SystemUserRoleResp] { data =>
      systemUserApi.removeSystemUserRoles(systemId, userId, data)
    }
  }
}
