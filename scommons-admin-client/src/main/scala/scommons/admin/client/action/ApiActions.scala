package scommons.admin.client.action

import org.scalajs.dom
import scommons.admin.client.api.AdminUiApiClient
import scommons.admin.client.company.action._
import scommons.api.http.js.JsApiHttpClient

object ApiActions extends CompanyActions {

  private val baseUrl = {
    val loc = dom.window.location
    s"${loc.protocol}//${loc.host}/scommons-admin/ui"
  }

  protected val client: AdminUiApiClient = {
    new AdminUiApiClient(new JsApiHttpClient(baseUrl))
  }
}
