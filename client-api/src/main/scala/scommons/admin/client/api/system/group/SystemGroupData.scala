package scommons.admin.client.api.system.group

import org.joda.time.DateTime
import play.api.libs.json._

case class SystemGroupData(id: Option[Int],
                           name: String,
                           updatedAt: Option[DateTime] = None,
                           createdAt: Option[DateTime] = None,
                           version: Option[Int] = None)

object SystemGroupData {

  import scommons.api.jodatime.JodaTimeImplicits.{dateTimeReads => dtReads, dateTimeWrites => dtWrites}

  implicit val jsonFormat: Format[SystemGroupData] =
    Json.format[SystemGroupData]
}
