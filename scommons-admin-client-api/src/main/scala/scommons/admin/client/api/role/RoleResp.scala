package scommons.admin.client.api.role

import play.api.libs.json._
import scommons.api.{ApiStatus, DataResponse}

case class RoleResp private(status: ApiStatus,
                            data: Option[RoleData]
                           ) extends DataResponse[RoleData]

object RoleResp {

  implicit val jsonFormat: Format[RoleResp] =
    Json.format[RoleResp]

  def apply(status: ApiStatus): RoleResp =
    RoleResp(status, None)

  def apply(data: RoleData): RoleResp =
    RoleResp(ApiStatus.Ok, Some(data))
}
