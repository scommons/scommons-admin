package scommons.admin.client.api.company

import play.api.libs.json._
import scommons.api.{ApiStatus, DataResponse}

case class CompanyResp private(status: ApiStatus,
                               data: Option[CompanyData]
                              ) extends DataResponse[CompanyData]

object CompanyResp {

  implicit val jsonFormat: Format[CompanyResp] =
    Json.format[CompanyResp]

  def apply(status: ApiStatus): CompanyResp =
    CompanyResp(status, None)

  def apply(data: CompanyData): CompanyResp =
    CompanyResp(ApiStatus.Ok, Some(data))
}
