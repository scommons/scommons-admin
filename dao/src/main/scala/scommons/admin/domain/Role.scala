package scommons.admin.domain

import org.joda.time.{DateTime, DateTimeZone}

case class Role(id: Int,
                systemId: Int,
                bitIndex: Int,
                title: String,
                updatedBy: Int,
                updatedAt: DateTime = Role.defaultDateTime,
                createdAt: DateTime = Role.defaultDateTime,
                version: Int = 1)

object Role {
  private val defaultDateTime: DateTime = new DateTime(0, DateTimeZone.UTC)
}

//noinspection TypeAnnotation
trait RoleSchema {

  val ctx: AdminDBContext
  import ctx._

  implicit val rolesInsertMeta = insertMeta[Role](
    _.id, _.version, _.createdAt, _.updatedAt
  )
  implicit val rolesUpdateMeta = updateMeta[Role](
    _.id, _.version, _.createdAt, _.updatedAt
  )

  val roles = quote {
    querySchema[Role](
      "roles",
      _.id -> "id",
      _.systemId -> "system_id",
      _.bitIndex -> "bit_index",
      _.title -> "title",
      _.updatedBy -> "updated_by",
      _.updatedAt -> "updated_at",
      _.createdAt -> "created_at",
      _.version -> "version"
    )
  }
}
