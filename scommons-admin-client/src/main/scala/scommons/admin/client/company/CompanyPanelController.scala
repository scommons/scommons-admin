package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.ReactRedux
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminState
import scommons.admin.client.action.ApiActions

object CompanyPanelController {

  def apply(): ReactClass = reactClass
  private lazy val reactClass = createComp

  private def createComp = ReactRedux.connectAdvanced(
    (dispatch: Dispatch) => {
      (state: AdminState, _: Props[Unit]) => {
        CompanyPanelProps(dispatch, ApiActions, state.companyState)
      }
    }
  )(CompanyPanel())
}
