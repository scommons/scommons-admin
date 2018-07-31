package scommons.admin.client.api.system

import play.api.libs.json._
import scommons.api.{ApiStatus, DataListResponse}

case class SystemListResp private(status: ApiStatus,
                                  dataList: Option[List[SystemData]]
                                 ) extends DataListResponse[SystemData]

object SystemListResp {

  implicit val jsonFormat: Format[SystemListResp] =
    Json.format[SystemListResp]

  def apply(dataList: List[SystemData]): SystemListResp =
    SystemListResp(ApiStatus.Ok, Some(dataList))
}
