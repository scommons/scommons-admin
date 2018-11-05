package scommons.admin.domain

case class SystemUser(systemId: Int,
                      userId: Int,
                      roles: Option[Long] = None,
                      inheritedRoles: Option[Long] = None,
                      parentId: Option[Long] = None)

//noinspection TypeAnnotation
trait SystemUserSchema {

  val ctx: AdminDBContext
  import ctx._

  val systemsUsers = quote {
    querySchema[SystemUser](
      "systems_users",
      _.systemId -> "system_id",
      _.userId -> "user_id",
      _.roles -> "roles",
      _.inheritedRoles -> "inherited_roles",
      _.parentId -> "parent_id"
    )
  }
}
