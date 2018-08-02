package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminStateDef
import scommons.admin.client.system.group.SystemGroupController.extractGroupId
import scommons.admin.client.system.group.action.SystemGroupActions
import scommons.client.app.BaseStateAndRouteController
import scommons.client.util.PathParamsExtractors

class SystemGroupController(apiActions: SystemGroupActions)
  extends BaseStateAndRouteController[AdminStateDef, SystemGroupPanelProps] {

  lazy val component: ReactClass = SystemGroupPanel()

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              props: Props[Unit],
                              routerProps: RouterProps): SystemGroupPanelProps = {

    val path = routerProps.location.pathname
    
    SystemGroupPanelProps(dispatch, apiActions, state.systemGroupState, extractGroupId(path))
  }
}

object SystemGroupController {
  
  val path = "/apps"
  
  private val groupIdRegex = s"$path/(\\d+)".r
  
  def extractGroupId(path: String): Option[Int] = {
    PathParamsExtractors.extractId(groupIdRegex, path)
  }
}
