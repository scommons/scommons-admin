package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.company.CompanyController
import scommons.admin.client.role.RoleActions._
import scommons.admin.client.role.RoleController
import scommons.admin.client.system.SystemController
import scommons.admin.client.system.group.SystemGroupController
import scommons.client.app._
import scommons.client.ui.Buttons
import scommons.client.ui.tree._
import scommons.client.util.{ActionsData, BrowsePath}

class AdminRouteController(apiActions: AdminActions,
                           companyController: CompanyController,
                           systemGroupController: SystemGroupController,
                           systemController: SystemController
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
              systemNode.copy(
                children = List(
                  getRolesNode(systemNode.path.value, system, roles)
                )
              )
            }
          )
        }
      )
    )
  }

  private lazy val rolesNode = BrowseTreeNodeData(
    "Roles",
    BrowsePath("/"),
    Some(AdminImagesCss.role),
    ActionsData(Set(Buttons.REFRESH.command, Buttons.ADD.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.roleListFetch(dispatch))
      case Buttons.ADD.command => dispatch(RoleCreateRequestAction(create = true))
    }),
    None
  )

  def getRolesNode(appPath: String, data: SystemData, roles: List[RoleData]): BrowseTreeNodeData = {
    val rolesPath = s"$appPath/${RoleController.pathName}"
    
    rolesNode.copy(
      path = BrowsePath(rolesPath),
      children = roles.map(getRoleItem(rolesPath, _))
    )
  }

  private lazy val roleItem = BrowseTreeItemData(
    "",
    BrowsePath("/"),
    Some(AdminImagesCss.role),
    ActionsData(Set(Buttons.EDIT.command), dispatch => {
      case Buttons.EDIT.command => dispatch(RoleUpdateRequestAction(update = true))
    }),
    None
  )

  def getRoleItem(rolesPath: String, data: RoleData): BrowseTreeItemData = {
    roleItem.copy(
      text = data.title,
      path = BrowsePath(s"$rolesPath/${data.id.get}")
    )
  }
}
