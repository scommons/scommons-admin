package scommons.admin.domain

import org.joda.time.{DateTime, DateTimeZone}

case class SystemUser(systemId: Int,
                      userId: Int,
                      updatedBy: Int,
                      roles: Long = 0L,
                      inheritedRoles: Long = 0L,
                      parentId: Option[Long] = None,
                      updatedAt: DateTime = SystemUser.defaultDateTime,
                      createdAt: DateTime = SystemUser.defaultDateTime,
                      version: Int = 1)

object SystemUser {
  private val defaultDateTime: DateTime = new DateTime(0, DateTimeZone.UTC)
}

//noinspection TypeAnnotation
trait SystemUserSchema {

  val ctx: AdminDBContext
  import ctx._

  implicit val rolesInsertMeta = insertMeta[SystemUser](
    _.version, _.createdAt, _.updatedAt
  )
  implicit val rolesUpdateMeta = updateMeta[SystemUser](
    _.version, _.createdAt, _.updatedAt
  )

  val systemsUsers = quote {
    querySchema[SystemUser](
      "systems_users",
      _.systemId -> "system_id",
      _.userId -> "user_id",
      _.roles -> "roles",
      _.inheritedRoles -> "inherited_roles",
      _.parentId -> "parent_id",
      _.updatedBy -> "updated_by",
      _.updatedAt -> "updated_at",
      _.createdAt -> "created_at",
      _.version -> "version"
    )
  }
}
