package modules

import play.api.Configuration
import scaldi.Module
import scommons.admin.domain.AdminDBContext

class ApplicationModule extends Module
  with CompanyModule
  with SystemGroupModule
  with SystemModule {

  bind[AdminDBContext] to new AdminDBContext(
    inject[Configuration].get[Configuration]("quill.db").underlying
  )
}
