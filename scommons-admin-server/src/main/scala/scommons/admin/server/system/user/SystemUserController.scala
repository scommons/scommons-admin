package scommons.admin.server.system.user

import play.api.mvc._
import scommons.admin.client.api.system.user._
import scommons.play.controllers.BaseApiController

import scala.concurrent.ExecutionContext

class SystemUserController(systemUserApi: SystemUserApi)
                          (implicit components: ControllerComponents, ec: ExecutionContext)
  extends BaseApiController(components) {

  def list(id: Int,
           offset: Option[Int],
           limit: Option[Int],
           symbols: Option[String]): Action[AnyContent] = {
    
    apiNoBodyAction[SystemUserListResp] {
      systemUserApi.listSystemUsers(id, offset, limit, symbols)
    }
  }
}
