package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.system.SystemActions
import scommons.admin.client.system.SystemActions.SystemCreateRequestAction
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.admin.client.system.group.SystemGroupController.extractGroupId
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.app.BaseStateAndRouteController
import scommons.client.ui.tree.BrowseTreeNodeData
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.{ActionsData, BrowsePath, PathParamsExtractors}

class SystemGroupController(groupActions: SystemGroupActions, systemActions: SystemActions)
  extends BaseStateAndRouteController[AdminStateDef, SystemGroupPanelProps] {

  lazy val component: ReactClass = SystemGroupPanel()

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              props: Props[Unit],
                              routerProps: RouterProps): SystemGroupPanelProps = {

    val path = routerProps.location.pathname
    
    SystemGroupPanelProps(dispatch, groupActions, state.systemGroupState, extractGroupId(path))
  }

  private lazy val applicationsNode = BrowseTreeNodeData(
    "Applications",
    BrowsePath(SystemGroupController.path),
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

  def getApplicationsNode: BrowseTreeNodeData = applicationsNode

  def getEnvironmentNode(data: SystemGroupData): BrowseTreeNodeData = environmentNode.copy(
    text = data.name,
    path = BrowsePath(s"${SystemGroupController.path}/${data.id.get}")
  )
}

object SystemGroupController {
  
  val path = "/apps"
  
  private val groupIdRegex = s"$path/(\\d+)".r
  
  def extractGroupId(path: String): Option[Int] = {
    PathParamsExtractors.extractId(groupIdRegex, path)
  }
}
