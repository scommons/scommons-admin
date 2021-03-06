package scommons.admin.client

import org.scalajs.dom
import scommons.admin.client.api.AdminUiApiClient
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.role.RoleActions
import scommons.admin.client.role.permission.RolePermissionActions
import scommons.admin.client.system.SystemActions
import scommons.admin.client.system.group.SystemGroupActions
import scommons.admin.client.system.user.SystemUserActions
import scommons.admin.client.user.UserActions
import scommons.admin.client.user.system.UserSystemActions
import scommons.api.http.xhr.XhrApiHttpClient

object AdminActions extends CompanyActions
  with SystemGroupActions
  with SystemActions
  with SystemUserActions
  with RoleActions
  with RolePermissionActions
  with UserActions
  with UserSystemActions {
  
  private val baseUrl = {
    val loc = dom.window.location
    s"${loc.protocol}//${loc.host}/scommons-admin/ui"
  }

  protected val client: AdminUiApiClient = {
    new AdminUiApiClient(new XhrApiHttpClient(baseUrl))
  }
}
