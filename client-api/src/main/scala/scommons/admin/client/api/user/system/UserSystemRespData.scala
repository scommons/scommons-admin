package scommons.admin.client.api.user.system

import play.api.libs.json._
import scommons.admin.client.api.user.UserData

case class UserSystemRespData(systems: List[UserSystemData],
                              user: UserData)

object UserSystemRespData {

  implicit val jsonFormat: Format[UserSystemRespData] =
    Json.format[UserSystemRespData]
}
