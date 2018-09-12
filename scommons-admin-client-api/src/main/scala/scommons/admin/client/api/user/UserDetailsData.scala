package scommons.admin.client.api.user

import play.api.libs.json._

case class UserDetailsData(user: UserData, profile: UserProfileData)

object UserDetailsData {

  implicit val jsonFormat: Format[UserDetailsData] =
    Json.format[UserDetailsData]
}
