package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminStateDef
import scommons.admin.client.role.RoleController._
import scommons.admin.client.role.action.RoleActions
import scommons.admin.client.system.SystemController.extractSystemId
import scommons.admin.client.system.group.SystemGroupController
import scommons.client.app.BaseStateAndRouteController
import scommons.client.util.PathParamsExtractors

class RoleController(apiActions: RoleActions)
  extends BaseStateAndRouteController[AdminStateDef, RolePanelProps]
    with PathParamsExtractors {

  lazy val component: ReactClass = RolePanel()

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: AdminStateDef,
                              props: Props[Unit],
                              routerProps: RouterProps): RolePanelProps = {

    val path = routerProps.location.pathname

    RolePanelProps(dispatch, apiActions, state.roleState,
      extractSystemId(path), extractId(roleIdRegex, path))
  }
}

object RoleController {
  
  val pathName = "roles"
  
  private val roleIdRegex = s"${SystemGroupController.path}/\\d+/\\d+/$pathName/(\\d+)".r
}
