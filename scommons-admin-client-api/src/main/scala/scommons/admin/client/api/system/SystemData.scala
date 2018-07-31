package scommons.admin.client.api.system

import org.joda.time.DateTime
import play.api.libs.json._

case class SystemData(id: Option[Int],
                      name: String,
                      password: String,
                      title: String,
                      url: String,
                      parentId: Int,
                      updatedAt: Option[DateTime] = None,
                      createdAt: Option[DateTime] = None,
                      version: Option[Int] = None)

object SystemData {

  import scommons.api.jodatime.JodaTimeImplicits.{dateTimeReads => dtReads, dateTimeWrites => dtWrites}

  implicit val jsonFormat: Format[SystemData] =
    Json.format[SystemData]
}
