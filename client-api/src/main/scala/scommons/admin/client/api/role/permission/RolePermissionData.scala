package scommons.admin.client.api.role.permission

import play.api.libs.json._

case class RolePermissionData(id: Int,
                              parentId: Option[Int],
                              isNode: Boolean,
                              title: String,
                              isEnabled: Boolean)

object RolePermissionData {

  implicit val jsonFormat: Format[RolePermissionData] =
    Json.format[RolePermissionData]
}
