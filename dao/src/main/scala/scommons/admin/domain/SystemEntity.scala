package scommons.admin.domain

import org.joda.time.{DateTime, DateTimeZone}

case class SystemEntity(id: Int,
                        name: String,
                        password: String,
                        title: String,
                        url: String,
                        parentId: Int,
                        updatedBy: Int,
                        updatedAt: DateTime = SystemEntity.defaultDateTime,
                        createdAt: DateTime = SystemEntity.defaultDateTime,
                        version: Int = 1)

object SystemEntity {
  private val defaultDateTime: DateTime = new DateTime(0, DateTimeZone.UTC)
}

//noinspection TypeAnnotation
trait SystemSchema {

  val ctx: AdminDBContext
  import ctx._

  implicit val systemsInsertMeta = insertMeta[SystemEntity](
    _.id, _.version, _.createdAt, _.updatedAt
  )
  implicit val systemsUpdateMeta = updateMeta[SystemEntity](
    _.id, _.version, _.createdAt, _.updatedAt
  )

  val systems = quote {
    querySchema[SystemEntity](
      "systems",
      _.id -> "id",
      _.name -> "name",
      _.password -> "password",
      _.title -> "title",
      _.url -> "url",
      _.parentId -> "parent_id",
      _.updatedBy -> "updated_by",
      _.updatedAt -> "updated_at",
      _.createdAt -> "created_at",
      _.version -> "version"
    )
  }
}
