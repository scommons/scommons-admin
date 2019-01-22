package scommons.admin.service.api.permission

import play.api.libs.json._

case class PermissionData(name: String,
                          title: String,
                          roles: Option[List[Long]] = None)

object PermissionData {

  implicit val jsonFormat: Format[PermissionData] =
    Json.format[PermissionData]
}
