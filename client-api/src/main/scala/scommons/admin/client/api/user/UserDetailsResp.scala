package scommons.admin.client.api.user

import play.api.libs.json._
import scommons.api.{ApiStatus, DataResponse}

case class UserDetailsResp private(status: ApiStatus,
                                   data: Option[UserDetailsData]
                                  ) extends DataResponse[UserDetailsData]

object UserDetailsResp {

  implicit val jsonFormat: Format[UserDetailsResp] =
    Json.format[UserDetailsResp]

  def apply(status: ApiStatus): UserDetailsResp =
    UserDetailsResp(status, None)

  def apply(data: UserDetailsData): UserDetailsResp =
    UserDetailsResp(ApiStatus.Ok, Some(data))
}
