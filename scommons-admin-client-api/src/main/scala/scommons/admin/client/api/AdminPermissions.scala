package scommons.admin.client.api

import scommons.admin.client.api.role.RolePermissions
import scommons.admin.client.api.system.SystemPermissions
import scommons.admin.client.api.user.UserPermissions
import scommons.api.permission._

object AdminPermissions extends PermissionNode("Admin", "Admin permissions", List(
  UserPermissions,
  RolePermissions,
  SystemPermissions
))
