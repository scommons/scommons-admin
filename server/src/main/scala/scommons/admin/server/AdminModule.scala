package scommons.admin.server

import play.api.Configuration
import scaldi.Module
import scommons.admin.domain.AdminDBContext
import scommons.admin.server.company.CompanyModule
import scommons.admin.server.role.RoleModule
import scommons.admin.server.role.permission.RolePermissionModule
import scommons.admin.server.system.SystemModule
import scommons.admin.server.system.group.SystemGroupModule
import scommons.admin.server.system.user.SystemUserModule
import scommons.admin.server.user.UserModule
import scommons.admin.server.user.system.UserSystemModule

class AdminModule extends Module
  with CompanyModule
  with SystemGroupModule
  with SystemModule
  with SystemUserModule
  with RoleModule
  with RolePermissionModule
  with UserModule
  with UserSystemModule {

  bind[AdminDBContext] to new AdminDBContext(
    inject[Configuration].get[Configuration]("quill.db").underlying
  )
}
