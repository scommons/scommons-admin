package scommons.admin.client.api.system.user

import play.api.libs.json._
import scommons.api.{ApiStatus, PageDataResponse}

case class SystemUserListResp private(status: ApiStatus,
                                      dataList: Option[List[SystemUserData]],
                                      totalCount: Option[Int]
                                     ) extends PageDataResponse[SystemUserData]

object SystemUserListResp {

  implicit val jsonFormat: Format[SystemUserListResp] =
    Json.format[SystemUserListResp]

  def apply(status: ApiStatus): SystemUserListResp =
    SystemUserListResp(status, None, None)

  def apply(dataList: List[SystemUserData], totalCount: Option[Int]): SystemUserListResp =
    SystemUserListResp(ApiStatus.Ok, Some(dataList), totalCount)
}
