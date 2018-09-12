package scommons.admin.client.api.user

import play.api.libs.json._
import scommons.api.{ApiStatus, PageDataResponse}

case class UserListResp private(status: ApiStatus,
                                dataList: Option[List[UserData]],
                                totalCount: Option[Int]
                               ) extends PageDataResponse[UserData]

object UserListResp {

  implicit val jsonFormat: Format[UserListResp] =
    Json.format[UserListResp]

  def apply(dataList: List[UserData], totalCount: Option[Int]): UserListResp =
    UserListResp(ApiStatus.Ok, Some(dataList), totalCount)
}
