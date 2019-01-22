package scommons.admin.client.api.user

import org.joda.time.DateTime
import play.api.libs.json._

case class UserData(id: Option[Int],
                    company: UserCompanyData,
                    login: String,
                    password: String,
                    active: Boolean,
                    lastLoginDate: Option[DateTime] = None,
                    updatedAt: Option[DateTime] = None,
                    createdAt: Option[DateTime] = None,
                    version: Option[Int] = None)

object UserData {

  import scommons.api.jodatime.JodaTimeImplicits.{dateTimeReads => dtReads, dateTimeWrites => dtWrites}

  implicit val jsonFormat: Format[UserData] =
    Json.format[UserData]
}
