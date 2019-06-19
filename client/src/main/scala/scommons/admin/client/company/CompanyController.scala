package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminStateDef
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.{ActionsData, BrowsePath}
import scommons.react.UiComponent
import scommons.react.redux.BaseStateController

class CompanyController(apiActions: CompanyActions)
  extends BaseStateController[AdminStateDef, CompanyPanelProps] {

  lazy val uiComponent: UiComponent[CompanyPanelProps] = CompanyPanel

  def mapStateToProps(dispatch: Dispatch, state: AdminStateDef, props: Props[Unit]): CompanyPanelProps = {
    CompanyPanelProps(dispatch, apiActions, state.companyState)
  }

  private lazy val companiesItem = BrowseTreeItemData(
    "Companies",
    BrowsePath("/"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.REFRESH.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.companyListFetch(dispatch, None, None))
    }),
    Some(apply())
  )

  def getCompaniesItem(path: BrowsePath): BrowseTreeItemData = companiesItem.copy(
    path = path
  )
}
