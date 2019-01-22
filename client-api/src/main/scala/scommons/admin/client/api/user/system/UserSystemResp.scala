package scommons.admin.client.api.user.system

import play.api.libs.json._
import scommons.api.{ApiStatus, DataResponse}

case class UserSystemResp private(status: ApiStatus,
                                  data: Option[UserSystemRespData]
                                 ) extends DataResponse[UserSystemRespData]

object UserSystemResp {

  implicit val jsonFormat: Format[UserSystemResp] =
    Json.format[UserSystemResp]

  def apply(status: ApiStatus): UserSystemResp =
    UserSystemResp(status, None)

  def apply(data: UserSystemRespData): UserSystemResp =
    UserSystemResp(ApiStatus.Ok, Some(data))
}
