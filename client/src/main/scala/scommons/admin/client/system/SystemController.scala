package scommons.admin.client.system

import scommons.admin.client.AdminRouteController._
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.SystemActions.SystemUpdateRequestAction
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{BaseStateAndRouteController, RouteParams}
import scommons.client.ui.tree.BrowseTreeNodeData
import scommons.client.ui.Buttons
import scommons.client.util.{ActionsData, BrowsePath}
import scommons.react.UiComponent
import scommons.react.redux.Dispatch

class SystemController(apiActions: SystemActions)
  extends BaseStateAndRouteController[AdminStateDef, SystemPanelProps] {

  lazy val uiComponent: UiComponent[SystemPanelProps] = SystemPanel

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              routeParams: RouteParams): SystemPanelProps = {

    val pathParams = routeParams.pathParams
    
    SystemPanelProps(dispatch, apiActions, state.systemState,
      extractSystemGroupId(pathParams), extractSystemId(pathParams, exact = true))
  }

  private lazy val applicationNode = BrowseTreeNodeData(
    "",
    BrowsePath("/"),
    Some(AdminImagesCss.computer),
    ActionsData(Set(Buttons.EDIT.command), dispatch => {
      case Buttons.EDIT.command => dispatch(SystemUpdateRequestAction(update = true))
    }),
    None
  )

  def getApplicationNode(path: BrowsePath, data: SystemData): BrowseTreeNodeData = {
    applicationNode.copy(
      text = data.name,
      path = BrowsePath(s"$path/${data.id.get}")
    )
  }
}
