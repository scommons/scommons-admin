package scommons.admin.client.api.company

import play.api.libs.json._
import scommons.api.{ApiStatus, PageDataResponse}

case class CompanyListResp private(status: ApiStatus,
                                   dataList: Option[List[CompanyData]],
                                   totalCount: Option[Int]
                                  ) extends PageDataResponse[CompanyData]

object CompanyListResp {

  implicit val jsonFormat: Format[CompanyListResp] =
    Json.format[CompanyListResp]

  def apply(status: ApiStatus): CompanyListResp =
    CompanyListResp(status, None, None)

  def apply(dataList: List[CompanyData], totalCount: Option[Int] = None): CompanyListResp =
    CompanyListResp(ApiStatus.Ok, Some(dataList), totalCount)
}
