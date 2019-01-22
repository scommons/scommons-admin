package scommons.admin.client.api.user.system

import play.api.libs.json._

case class UserSystemData(id: Int,
                          name: String,
                          isSelected: Boolean)

object UserSystemData {

  implicit val jsonFormat: Format[UserSystemData] =
    Json.format[UserSystemData]
}
