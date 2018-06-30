package scommons.admin.client.company.action

import io.github.shogowada.scalajs.reactjs.redux.Action
import scommons.admin.client.api.company.CompanyData

case class CompanyUpdatedAction(data: CompanyData) extends Action
