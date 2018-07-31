package scommons.admin.client.api.system

import play.api.libs.json._
import scommons.api.{ApiStatus, DataResponse}

case class SystemResp private(status: ApiStatus,
                              data: Option[SystemData]
                             ) extends DataResponse[SystemData]

object SystemResp {

  implicit val jsonFormat: Format[SystemResp] =
    Json.format[SystemResp]

  def apply(status: ApiStatus): SystemResp =
    SystemResp(status, None)

  def apply(data: SystemData): SystemResp =
    SystemResp(ApiStatus.Ok, Some(data))
}
