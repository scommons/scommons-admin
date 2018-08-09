package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.system.SystemActions
import scommons.admin.client.system.SystemActions.SystemCreateRequestAction
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{BaseStateAndRouteController, RouteParams}
import scommons.client.ui.tree.BrowseTreeNodeData
import scommons.client.ui.{ButtonImagesCss, Buttons, UiComponent}
import scommons.client.util.{ActionsData, BrowsePath}

class SystemGroupController(groupActions: SystemGroupActions, systemActions: SystemActions)
  extends BaseStateAndRouteController[AdminStateDef, SystemGroupPanelProps] {

  lazy val uiComponent: UiComponent[SystemGroupPanelProps] = SystemGroupPanel

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              routeParams: RouteParams): SystemGroupPanelProps = {

    val path = routeParams.path
    
    SystemGroupPanelProps(dispatch, groupActions, state.systemGroupState, extractGroupId(path))
  }

  private lazy val applicationsNode = BrowseTreeNodeData(
    "Applications",
    BrowsePath("/"),
    Some(AdminImagesCss.computer),
    ActionsData(Set(Buttons.REFRESH.command, Buttons.ADD.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(groupActions.systemGroupListFetch(dispatch))
      case Buttons.ADD.command => dispatch(SystemGroupCreateRequestAction(create = true))
    }),
    None
  )

  private lazy val environmentNode = BrowseTreeNodeData(
    "",
    BrowsePath("/"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.REFRESH.command, Buttons.ADD.command, Buttons.EDIT.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(systemActions.systemListFetch(dispatch))
      case Buttons.ADD.command => dispatch(SystemCreateRequestAction(create = true))
      case Buttons.EDIT.command => dispatch(SystemGroupUpdateRequestAction(update = true))
    }),
    None
  )

  def getApplicationsNode(path: BrowsePath): BrowseTreeNodeData = applicationsNode.copy(
    path = path
  )

  def getEnvironmentNode(path: BrowsePath, data: SystemGroupData): BrowseTreeNodeData = environmentNode.copy(
    text = data.name,
    path = BrowsePath(s"$path/${data.id.get}")
  )
}
