package scommons.admin.domain

case class RolePermission(roleId: Int,
                          permissionId: Int)

//noinspection TypeAnnotation
trait RolePermissionSchema {

  val ctx: AdminDBContext
  import ctx._

  val rolesPermissions = quote {
    querySchema[RolePermission](
      "roles_permissions",
      _.roleId -> "role_id",
      _.permissionId -> "permission_id"
    )
  }
}
