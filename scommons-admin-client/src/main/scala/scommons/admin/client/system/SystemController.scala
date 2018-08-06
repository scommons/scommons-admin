package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminStateDef
import scommons.admin.client.system.SystemController.extractSystemId
import scommons.admin.client.system.group.SystemGroupController
import scommons.admin.client.system.group.SystemGroupController.extractGroupId
import scommons.client.app.BaseStateAndRouteController
import scommons.client.util.PathParamsExtractors

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
}

object SystemController {
  
  private val systemIdRegex = s"${SystemGroupController.path}/\\d+/(\\d+)".r

  def extractSystemId(path: String, exact: Boolean = false): Option[Int] = {
    PathParamsExtractors.extractId(systemIdRegex, path, exact)
  }
}
