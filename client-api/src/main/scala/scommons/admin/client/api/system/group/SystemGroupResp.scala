package scommons.admin.client.api.system.group

import play.api.libs.json._
import scommons.api.{ApiStatus, DataResponse}

case class SystemGroupResp private(status: ApiStatus,
                                   data: Option[SystemGroupData]
                                  ) extends DataResponse[SystemGroupData]

object SystemGroupResp {

  implicit val jsonFormat: Format[SystemGroupResp] =
    Json.format[SystemGroupResp]

  def apply(status: ApiStatus): SystemGroupResp =
    SystemGroupResp(status, None)

  def apply(data: SystemGroupData): SystemGroupResp =
    SystemGroupResp(ApiStatus.Ok, Some(data))
}
