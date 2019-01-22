package scommons.admin.client.api.system.user

import play.api.libs.json._

case class SystemUserRoleUpdateReq(roleIds: Set[Int],
                                   version: Int)

object SystemUserRoleUpdateReq {

  implicit val jsonFormat: Format[SystemUserRoleUpdateReq] =
    Json.format[SystemUserRoleUpdateReq]
}
