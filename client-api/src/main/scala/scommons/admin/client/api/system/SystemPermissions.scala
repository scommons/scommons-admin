package scommons.admin.client.api.system

import scommons.api.admin.permission._

object SystemPermissions extends PermissionNode("System", "Systems") {

  val read = add(Permission.read)

  val create = add(Permission.create)

  val update = add(Permission.update)
}
