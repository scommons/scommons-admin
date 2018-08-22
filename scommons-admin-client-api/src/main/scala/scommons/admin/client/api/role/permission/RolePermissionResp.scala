package scommons.admin.client.api.role.permission

import play.api.libs.json._
import scommons.api.{ApiStatus, DataResponse}

case class RolePermissionResp private(status: ApiStatus,
                                      data: Option[RolePermissionRespData]
                                     ) extends DataResponse[RolePermissionRespData]

object RolePermissionResp {

  implicit val jsonFormat: Format[RolePermissionResp] =
    Json.format[RolePermissionResp]

  def apply(status: ApiStatus): RolePermissionResp =
    RolePermissionResp(status, None)

  def apply(data: RolePermissionRespData): RolePermissionResp =
    RolePermissionResp(ApiStatus.Ok, Some(data))
}
