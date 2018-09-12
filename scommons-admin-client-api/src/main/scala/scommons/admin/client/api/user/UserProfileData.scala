package scommons.admin.client.api.user

import org.joda.time.DateTime
import play.api.libs.json._

case class UserProfileData(email: String,
                           firstName: String,
                           lastName: String,
                           phone: Option[String],
                           updatedAt: Option[DateTime] = None,
                           createdAt: Option[DateTime] = None,
                           version: Option[Int] = None)

object UserProfileData {

  import scommons.api.jodatime.JodaTimeImplicits.{dateTimeReads => dtReads, dateTimeWrites => dtWrites}

  implicit val jsonFormat: Format[UserProfileData] =
    Json.format[UserProfileData]
}
