package scommons.admin.client.api.system.user

import play.api.libs.json._
import scommons.api.{ApiStatus, DataResponse}

case class SystemUserRoleResp private(status: ApiStatus,
                                      data: Option[SystemUserRoleRespData]
                                     ) extends DataResponse[SystemUserRoleRespData]

object SystemUserRoleResp {

  implicit val jsonFormat: Format[SystemUserRoleResp] =
    Json.format[SystemUserRoleResp]

  def apply(status: ApiStatus): SystemUserRoleResp =
    SystemUserRoleResp(status, None)

  def apply(data: SystemUserRoleRespData): SystemUserRoleResp =
    SystemUserRoleResp(ApiStatus.Ok, Some(data))
}
