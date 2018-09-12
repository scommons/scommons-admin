package scommons.admin.client.api.user

import play.api.libs.json._

case class UserCompanyData(id: Int, name: String)

object UserCompanyData {

  implicit val jsonFormat: Format[UserCompanyData] =
    Json.format[UserCompanyData]
}
