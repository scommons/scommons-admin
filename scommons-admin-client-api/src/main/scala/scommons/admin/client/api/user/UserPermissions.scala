package scommons.admin.client.api.user

import scommons.api.permission._

object UserPermissions extends PermissionNode("User", "Users") {

  val read = add(Permission.read)
  
  val create = add(Permission.create)
  
  val update = add(Permission.update)
  
  val assignPermissions = add(Permission("assignPermissions", "Assign permissions"))
  
  val assignRoles = add(Permission("assignRoles", "Assign roles"))
}
