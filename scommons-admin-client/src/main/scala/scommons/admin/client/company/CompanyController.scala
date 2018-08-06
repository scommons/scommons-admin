package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminStateDef
import scommons.client.app.BaseStateController
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.{ActionsData, BrowsePath}

class CompanyController(apiActions: CompanyActions)
  extends BaseStateController[AdminStateDef, CompanyPanelProps] {

  lazy val component: ReactClass = CompanyPanel()

  def mapStateToProps(dispatch: Dispatch, state: AdminStateDef, props: Props[Unit]): CompanyPanelProps = {
    CompanyPanelProps(dispatch, apiActions, state.companyState)
  }

  private lazy val companiesItem = BrowseTreeItemData(
    "Companies",
    BrowsePath("/companies"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.REFRESH.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.companyListFetch(dispatch, None, None))
    }),
    Some(apply())
  )

  def getCompaniesItem: BrowseTreeItemData = companiesItem
}
