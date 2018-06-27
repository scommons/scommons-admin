package scommons.admin.domain

import java.time.{LocalDateTime, ZoneId}

import com.typesafe.config.Config
import io.getquill.{PostgresAsyncContext, SnakeCase}
import org.joda.time.DateTime

//noinspection TypeAnnotation
class AdminDBContext(config: Config) extends PostgresAsyncContext[SnakeCase](config) {

  implicit val encodeDateTime = MappedEncoding[DateTime, LocalDateTime](d =>
    LocalDateTime.ofInstant(d.toDate.toInstant, ZoneId.of("UTC"))
  )

  implicit val decodeDateTime = MappedEncoding[LocalDateTime, DateTime](d =>
    new DateTime(d.atZone(ZoneId.of("UTC")).toInstant.toEpochMilli)
  )
}
