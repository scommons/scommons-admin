package scommons.admin.client.api.user.system

import play.api.libs.json._

case class UserSystemUpdateReq(systemIds: Set[Int],
                               version: Int)

object UserSystemUpdateReq {

  implicit val jsonFormat: Format[UserSystemUpdateReq] =
    Json.format[UserSystemUpdateReq]
}
