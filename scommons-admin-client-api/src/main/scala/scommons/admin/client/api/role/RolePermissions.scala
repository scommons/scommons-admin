package scommons.admin.client.api.role

import scommons.api.permission._

object RolePermissions extends PermissionNode("Roles", "Roles") {

  val rename = add(Permission.rename)

  val create = add(Permission.create)
}
