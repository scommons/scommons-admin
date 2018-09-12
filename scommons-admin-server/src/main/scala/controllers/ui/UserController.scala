package controllers.ui

import play.api.libs.json.JsValue
import play.api.mvc._
import scommons.admin.client.api.user._
import scommons.play.controllers.BaseApiController

import scala.concurrent.ExecutionContext

class UserController(userApi: UserApi)
                    (implicit components: ControllerComponents, ec: ExecutionContext)
  extends BaseApiController(components) {

  def getById(id: Int): Action[AnyContent] = {
    apiNoBodyAction[UserDetailsResp] {
      userApi.getUserById(id)
    }
  }

  def list(offset: Option[Int],
           limit: Option[Int],
           symbols: Option[String]): Action[AnyContent] = {
    
    apiNoBodyAction[UserListResp] {
      userApi.listUsers(offset, limit, symbols)
    }
  }

  def create(): Action[JsValue] = {
    apiAction[UserDetailsData, UserDetailsResp] { data =>
      userApi.createUser(data)
    }
  }

  def update(): Action[JsValue] = {
    apiAction[UserDetailsData, UserDetailsResp] { data =>
      userApi.updateUser(data)
    }
  }
}
