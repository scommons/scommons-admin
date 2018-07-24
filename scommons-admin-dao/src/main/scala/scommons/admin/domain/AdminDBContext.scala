package scommons.admin.domain

import com.typesafe.config.Config
import io.getquill.context.async.SqlTypes
import io.getquill.{PostgresAsyncContext, SnakeCase}
import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}

class AdminDBContext(config: Config) extends PostgresAsyncContext[SnakeCase](config) {

  implicit val jodaDateTimeDecoder: Decoder[DateTime] = decoder[DateTime]({
    case d: LocalDateTime => d.toDateTime(DateTimeZone.UTC)
  }, SqlTypes.TIMESTAMP)

  implicit val jodaDateTimeEncoder: Encoder[DateTime] = encoder[DateTime]({ (d: DateTime) =>
    d.withZone(DateTimeZone.UTC).toLocalDateTime
  }, SqlTypes.TIMESTAMP)
}
