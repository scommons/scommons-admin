package scommons.admin.domain

import org.joda.time.{DateTime, DateTimeZone}

case class SystemGroup(id: Int,
                       name: String,
                       updatedBy: Int,
                       updatedAt: DateTime = SystemGroup.defaultDateTime,
                       createdAt: DateTime = SystemGroup.defaultDateTime,
                       version: Int = 1)

object SystemGroup {
  private val defaultDateTime: DateTime = new DateTime(0, DateTimeZone.UTC)
}

//noinspection TypeAnnotation
trait SystemGroupSchema {

  val ctx: AdminDBContext
  import ctx._

  implicit val systemsGroupsInsertMeta = insertMeta[SystemGroup](
    _.id, _.version, _.createdAt, _.updatedAt
  )
  implicit val systemsGroupsUpdateMeta = updateMeta[SystemGroup](
    _.id, _.version, _.createdAt, _.updatedAt
  )

  val systemsGroups = quote {
    querySchema[SystemGroup](
      "systems_groups",
      _.id -> "id",
      _.name -> "name",
      _.updatedBy -> "updated_by",
      _.updatedAt -> "updated_at",
      _.createdAt -> "created_at",
      _.version -> "version"
    )
  }
}
