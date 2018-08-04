package scommons.admin.client.action

import org.scalajs.dom
import scommons.admin.client.api.AdminUiApiClient
import scommons.admin.client.company.action._
import scommons.admin.client.role.action.RoleActions
import scommons.admin.client.system.action.SystemActions
import scommons.admin.client.system.group.action.SystemGroupActions
import scommons.api.http.js.JsApiHttpClient

trait ApiActions extends CompanyActions
  with SystemGroupActions
  with SystemActions
  with RoleActions {
  
  private val baseUrl = {
    val loc = dom.window.location
    s"${loc.protocol}//${loc.host}/scommons-admin/ui"
  }

  protected val client: AdminUiApiClient = {
    new AdminUiApiClient(new JsApiHttpClient(baseUrl))
  }
}

object ApiActions extends ApiActions
