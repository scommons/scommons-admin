package scommons.admin.client.api.role.permission

import play.api.libs.json._
import scommons.admin.client.api.role.RoleData

case class RolePermissionRespData(permissions: List[RolePermissionData],
                                  role: RoleData)

object RolePermissionRespData {

  implicit val jsonFormat: Format[RolePermissionRespData] =
    Json.format[RolePermissionRespData]
}
