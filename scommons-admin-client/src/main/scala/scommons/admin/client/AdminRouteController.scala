package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.company.CompanyController
import scommons.admin.client.role.RoleController
import scommons.admin.client.system.SystemController
import scommons.admin.client.system.group.SystemGroupController
import scommons.client.app._
import scommons.client.ui.Buttons
import scommons.client.ui.tree._

class AdminRouteController(companyController: CompanyController,
                           systemGroupController: SystemGroupController,
                           systemController: SystemController,
                           roleController: RoleController
                          ) extends BaseStateController[AdminStateDef, AppBrowseControllerProps] {

  lazy val component: ReactClass = AppBrowseController()

  def mapStateToProps(dispatch: Dispatch, state: AdminStateDef, props: Props[Unit]): AppBrowseControllerProps = {
    val applicationsNode = systemGroupController.getApplicationsNode
    
    AppBrowseControllerProps(
      buttons = List(Buttons.REFRESH, Buttons.ADD, Buttons.REMOVE, Buttons.EDIT),
      treeRoots = getTreeRoots(state, applicationsNode),
      dispatch = dispatch,
      initiallyOpenedNodes = Set(applicationsNode.path)
    )
  }

  private def getTreeRoots(state: AdminStateDef, applicationsNode: BrowseTreeNodeData): List[BrowseTreeData] = {
    val systemGroupState = state.systemGroupState
    val systemState = state.systemState
    val roleState = state.roleState
    
    List(
      companyController.getCompaniesItem,
      applicationsNode.copy(
        children = systemGroupState.dataList.map { group =>
          val groupNode = systemGroupController.getEnvironmentNode(group)
          val systems = systemState.getSystems(group.id.get)
          groupNode.copy(
            children = systems.map { system =>
              val systemNode = systemController.getApplicationNode(groupNode.path.value, system)
              val roles = roleState.getRoles(system.id.get)
              val rolesNode = roleController.getRolesNode(systemNode.path.value)
              systemNode.copy(
                children = List(
                  rolesNode.copy(
                    children = roles.map(roleController.getRoleItem(rolesNode.path.value, _))
                  )
                )
              )
            }
          )
        }
      )
    )
  }
}
