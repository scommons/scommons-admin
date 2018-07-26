package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.action.ApiActions
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.company.CompanyPanelController
import scommons.admin.client.system.group.action._
import scommons.client.app._
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.ui.tree._
import scommons.client.util.{ActionsData, BrowsePath}

class AdminRouteController(apiActions: ApiActions)
  extends BaseStateController[AdminStateDef, AppBrowseControllerProps] {

  lazy val component: ReactClass = AppBrowseController()

  def mapStateToProps(dispatch: Dispatch)
                     (state: AdminStateDef, props: Props[Unit]): AppBrowseControllerProps = {

    AppBrowseControllerProps(
      buttons = List(Buttons.REFRESH, Buttons.ADD, Buttons.REMOVE, Buttons.EDIT),
      treeRoots = getTreeRoots(state),
      dispatch = dispatch,
      initiallyOpenedNodes = Set(environmentsNode.path)
    )
  }

  private def getTreeRoots(state: AdminStateDef): List[BrowseTreeData] = List(
    companiesItem,
    environmentsNode.copy(
      children = state.systemGroupState.dataList.map(getEnvironmentItem)
    )
  )

  lazy val companiesItem = BrowseTreeItemData(
    "Companies",
    BrowsePath("/companies"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.REFRESH.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.companyListFetch(dispatch, None, None))
    }),
    Some(new CompanyPanelController(apiActions)())
  )

  lazy val environmentsNode = BrowseTreeNodeData(
    "Environments",
    BrowsePath("/environments"),
    Some(AdminImagesCss.computer),
    ActionsData(Set(Buttons.REFRESH.command, Buttons.ADD.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.systemGroupListFetch(dispatch))
      case Buttons.ADD.command => dispatch(SystemGroupCreateRequestAction(create = true))
    }),
    None
  )
  
  private lazy val environmentItem = BrowseTreeItemData(
    "",
    BrowsePath("/"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.EDIT.command), dispatch => {
      case Buttons.EDIT.command => dispatch(SystemGroupUpdateRequestAction(update = true))
    }),
    None
  )

  def getEnvironmentItem(data: SystemGroupData): BrowseTreeItemData = {
    environmentItem.copy(
      text = data.name,
      path = BrowsePath(s"/environments/${data.id.get}")
    )
  }
}
