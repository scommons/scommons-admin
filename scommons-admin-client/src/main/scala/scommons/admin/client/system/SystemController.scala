package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminStateDef
import scommons.admin.client.system.group.SystemGroupController.extractId
import scommons.admin.client.system.SystemController._
import scommons.admin.client.system.action.SystemActions
import scommons.admin.client.system.group.SystemGroupController
import scommons.client.app.BaseStateAndRouteController

class SystemController(apiActions: SystemActions)
  extends BaseStateAndRouteController[AdminStateDef, SystemPanelProps] {

  lazy val component: ReactClass = SystemPanel()

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              props: Props[Unit],
                              routerProps: RouterProps): SystemPanelProps = {

    val path = routerProps.location.pathname
    
    SystemPanelProps(dispatch, apiActions, state.systemState,
      extractId(path), extractId(appIdRegex, path))
  }
}

object SystemController {
  
  private val appIdRegex = s"${SystemGroupController.path}/\\d+/(\\d+)".r
}
