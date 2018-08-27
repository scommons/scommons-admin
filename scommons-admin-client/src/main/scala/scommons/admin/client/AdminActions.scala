package scommons.admin.client

import org.scalajs.dom
import scommons.admin.client.api.AdminUiApiClient
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.role.RoleActions
import scommons.admin.client.role.permission.RolePermissionActions
import scommons.admin.client.system.SystemActions
import scommons.admin.client.system.group.SystemGroupActions
import scommons.api.http.js.JsApiHttpClient

object AdminActions extends CompanyActions
  with SystemGroupActions
  with SystemActions
  with RoleActions
  with RolePermissionActions {
  
  private val baseUrl = {
    val loc = dom.window.location
    s"${loc.protocol}//${loc.host}/scommons-admin/ui"
  }

  protected val client: AdminUiApiClient = {
    new AdminUiApiClient(new JsApiHttpClient(baseUrl))
  }
}
