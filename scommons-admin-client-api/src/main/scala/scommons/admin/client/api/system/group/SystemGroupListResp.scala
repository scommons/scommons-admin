package scommons.admin.client.api.system.group

import play.api.libs.json._
import scommons.api.{ApiStatus, DataListResponse}

case class SystemGroupListResp private(status: ApiStatus,
                                       dataList: Option[List[SystemGroupData]]
                                      ) extends DataListResponse[SystemGroupData]

object SystemGroupListResp {

  implicit val jsonFormat: Format[SystemGroupListResp] =
    Json.format[SystemGroupListResp]

  def apply(dataList: List[SystemGroupData]): SystemGroupListResp =
    SystemGroupListResp(ApiStatus.Ok, Some(dataList))
}
