package scommons.admin.server

import play.api.Configuration
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.server.company.CompanyModule
import scommons.admin.server.role.RoleModule
import scommons.admin.server.role.permission.RolePermissionModule
import scommons.admin.server.system.SystemModule
import scommons.admin.server.system.group.SystemGroupModule
import scommons.admin.server.user.UserModule

class AdminModule extends Module
  with CompanyModule
  with SystemGroupModule
  with SystemModule
  with RoleModule
  with RolePermissionModule
  with UserModule {

  bind[AdminDBContext] to new AdminDBContext(
    inject[Configuration].get[Configuration]("quill.db").underlying
  )
}
