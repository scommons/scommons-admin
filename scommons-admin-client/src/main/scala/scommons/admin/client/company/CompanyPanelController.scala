package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.company.action.CompanyActions
import scommons.admin.client.AdminStateDef
import scommons.client.app.BaseStateController

class CompanyPanelController(apiActions: CompanyActions)
  extends BaseStateController[AdminStateDef, CompanyPanelProps] {

  lazy val component: ReactClass = CompanyPanel()

  def mapStateToProps(dispatch: Dispatch, state: AdminStateDef, props: Props[Unit]): CompanyPanelProps = {
    CompanyPanelProps(dispatch, apiActions, state.companyState)
  }
}
