package scommons.admin.client.api.system.user

import org.joda.time.DateTime
import play.api.libs.json._

case class SystemUserData(userId: Int,
                          login: String,
                          lastLoginDate: Option[DateTime],
                          updatedAt: DateTime,
                          createdAt: DateTime,
                          version: Int)

object SystemUserData {

  import scommons.api.jodatime.JodaTimeImplicits.{dateTimeReads => dtReads, dateTimeWrites => dtWrites}

  implicit val jsonFormat: Format[SystemUserData] =
    Json.format[SystemUserData]
}
