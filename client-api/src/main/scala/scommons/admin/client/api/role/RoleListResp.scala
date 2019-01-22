package scommons.admin.client.api.role

import play.api.libs.json._
import scommons.api.{ApiStatus, DataListResponse}

case class RoleListResp private(status: ApiStatus,
                                dataList: Option[List[RoleData]]
                               ) extends DataListResponse[RoleData]

object RoleListResp {

  implicit val jsonFormat: Format[RoleListResp] =
    Json.format[RoleListResp]

  def apply(dataList: List[RoleData]): RoleListResp =
    RoleListResp(ApiStatus.Ok, Some(dataList))
}
