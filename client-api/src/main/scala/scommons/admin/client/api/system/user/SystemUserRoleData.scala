package scommons.admin.client.api.system.user

import play.api.libs.json._

case class SystemUserRoleData(id: Int,
                              title: String,
                              isSelected: Boolean)

object SystemUserRoleData {

  implicit val jsonFormat: Format[SystemUserRoleData] =
    Json.format[SystemUserRoleData]
}
