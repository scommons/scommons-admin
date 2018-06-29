package scommons.admin.client.api.company

import play.api.libs.json._

case class CompanyData(id: Option[Int], name: String)

object CompanyData {

  implicit val jsonFormat: Format[CompanyData] =
    Json.format[CompanyData]
}
