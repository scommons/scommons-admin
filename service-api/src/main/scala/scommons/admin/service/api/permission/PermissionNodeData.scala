package scommons.admin.service.api.permission

import play.api.libs.json._

case class PermissionNodeData(name: String,
                              title: String,
                              permissions: List[PermissionData],
                              nodes: Option[List[PermissionNodeData]] = None)

object PermissionNodeData {

  implicit val jsonFormat: Format[PermissionNodeData] =
    Json.format[PermissionNodeData]
}
