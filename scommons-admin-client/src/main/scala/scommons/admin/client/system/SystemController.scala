package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.SystemActions.SystemUpdateRequestAction
import scommons.admin.client.system.SystemController.extractSystemId
import scommons.admin.client.system.group.SystemGroupController
import scommons.admin.client.system.group.SystemGroupController.extractGroupId
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.app.BaseStateAndRouteController
import scommons.client.ui.Buttons
import scommons.client.ui.tree.BrowseTreeNodeData
import scommons.client.util.{ActionsData, BrowsePath, PathParamsExtractors}

class SystemController(apiActions: SystemActions)
  extends BaseStateAndRouteController[AdminStateDef, SystemPanelProps] {

  lazy val component: ReactClass = SystemPanel()

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              props: Props[Unit],
                              routerProps: RouterProps): SystemPanelProps = {

    val path = routerProps.location.pathname
    
    SystemPanelProps(dispatch, apiActions, state.systemState,
      extractGroupId(path), extractSystemId(path, exact = true))
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

  def getApplicationNode(parentPath: String, data: SystemData): BrowseTreeNodeData = {
    applicationNode.copy(
      text = data.name,
      path = BrowsePath(s"$parentPath/${data.id.get}")
    )
  }
}

object SystemController {
  
  private val systemIdRegex = s"${SystemGroupController.path}/\\d+/(\\d+)".r

  def extractSystemId(path: String, exact: Boolean = false): Option[Int] = {
    PathParamsExtractors.extractId(systemIdRegex, path, exact)
  }
}
