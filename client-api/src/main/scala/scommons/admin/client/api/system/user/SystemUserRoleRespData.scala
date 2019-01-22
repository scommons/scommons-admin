package scommons.admin.client.api.system.user

import play.api.libs.json._
import scommons.admin.client.api.role.permission.RolePermissionData

case class SystemUserRoleRespData(roles: List[SystemUserRoleData],
                                  permissions: List[RolePermissionData],
                                  systemUser: SystemUserData)

object SystemUserRoleRespData {

  implicit val jsonFormat: Format[SystemUserRoleRespData] =
    Json.format[SystemUserRoleRespData]
}
