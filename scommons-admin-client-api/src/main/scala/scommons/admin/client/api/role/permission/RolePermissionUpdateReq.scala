package scommons.admin.client.api.role.permission

import play.api.libs.json._

case class RolePermissionUpdateReq(permissionIds: Set[Int],
                                   version: Int)

object RolePermissionUpdateReq {

  implicit val jsonFormat: Format[RolePermissionUpdateReq] =
    Json.format[RolePermissionUpdateReq]
}
