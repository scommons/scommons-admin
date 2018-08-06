package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.company.CompanyPanelController
import scommons.admin.client.role.RoleActions._
import scommons.admin.client.role.RoleController
import scommons.admin.client.system.SystemActions._
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.admin.client.system.group.SystemGroupController
import scommons.client.app._
import scommons.client.ui.tree._
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.{ActionsData, BrowsePath}

class AdminRouteController(apiActions: AdminActions)
  extends BaseStateController[AdminStateDef, AppBrowseControllerProps] {

  lazy val component: ReactClass = AppBrowseController()

  def mapStateToProps(dispatch: Dispatch, state: AdminStateDef, props: Props[Unit]): AppBrowseControllerProps = {
    AppBrowseControllerProps(
      buttons = List(Buttons.REFRESH, Buttons.ADD, Buttons.REMOVE, Buttons.EDIT),
      treeRoots = getTreeRoots(state),
      dispatch = dispatch,
      initiallyOpenedNodes = Set(applicationsNode.path)
    )
  }

  private def getTreeRoots(state: AdminStateDef): List[BrowseTreeData] = {
    val systemsByParentId = state.systemState.systemsByParentId
    val rolesBySystemId = state.roleState.rolesBySystemId
    
    List(
      companiesItem,
      applicationsNode.copy(
        children = state.systemGroupState.dataList.map { group =>
          getEnvironmentNode(
            group,
            systemsByParentId.getOrElse(group.id.get, Nil),
            rolesBySystemId
          )
        }
      )
    )
  }

  lazy val companiesItem = BrowseTreeItemData(
    "Companies",
    BrowsePath("/companies"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.REFRESH.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.companyListFetch(dispatch, None, None))
    }),
    Some(new CompanyPanelController(apiActions)())
  )

  lazy val applicationsNode = BrowseTreeNodeData(
    "Applications",
    BrowsePath(SystemGroupController.path),
    Some(AdminImagesCss.computer),
    ActionsData(Set(Buttons.REFRESH.command, Buttons.ADD.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.systemGroupListFetch(dispatch))
      case Buttons.ADD.command => dispatch(SystemGroupCreateRequestAction(create = true))
    }),
    None
  )
  
  private lazy val environmentNode = BrowseTreeNodeData(
    "",
    BrowsePath("/"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.REFRESH.command, Buttons.ADD.command, Buttons.EDIT.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.systemListFetch(dispatch))
      case Buttons.ADD.command => dispatch(SystemCreateRequestAction(create = true))
      case Buttons.EDIT.command => dispatch(SystemGroupUpdateRequestAction(update = true))
    }),
    None
  )

  def getEnvironmentNode(data: SystemGroupData,
                         systems: List[SystemData],
                         rolesBySystemId: Map[Int, List[RoleData]]): BrowseTreeNodeData = {

    val parentPath = s"${SystemGroupController.path}/${data.id.get}"

    environmentNode.copy(
      text = data.name,
      path = BrowsePath(parentPath),
      children = systems.map { system =>
        getApplicationNode(parentPath, system, rolesBySystemId.getOrElse(system.id.get, Nil))
      }
    )
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

  def getApplicationNode(parentPath: String, data: SystemData, roles: List[RoleData]): BrowseTreeNodeData = {
    val appPath = s"$parentPath/${data.id.get}"
    
    applicationNode.copy(
      text = data.name,
      path = BrowsePath(appPath),
      children = List(
        getRolesNode(appPath, data, roles)
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
