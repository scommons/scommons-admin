package scommons.admin.client.api.role

import org.joda.time.DateTime
import play.api.libs.json._

case class RoleData(id: Option[Int],
                    systemId: Int,
                    title: String,
                    updatedAt: Option[DateTime] = None,
                    createdAt: Option[DateTime] = None,
                    version: Option[Int] = None)

object RoleData {

  import scommons.api.jodatime.JodaTimeImplicits.{dateTimeReads => dtReads, dateTimeWrites => dtWrites}

  implicit val jsonFormat: Format[RoleData] =
    Json.format[RoleData]
}
