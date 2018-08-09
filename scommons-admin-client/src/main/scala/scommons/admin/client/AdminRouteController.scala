package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.company.CompanyController
import scommons.admin.client.role.RoleController
import scommons.admin.client.system.SystemController
import scommons.admin.client.system.group.SystemGroupController
import scommons.client.app._
import scommons.client.controller.BaseStateController
import scommons.client.ui.tree._
import scommons.client.ui.{Buttons, UiComponent}
import scommons.client.util.{BrowsePath, PathParamsExtractors}

class AdminRouteController(companyController: CompanyController,
                           systemGroupController: SystemGroupController,
                           systemController: SystemController,
                           roleController: RoleController
                          ) extends BaseStateController[AdminStateDef, AppBrowseControllerProps] {

  lazy val uiComponent: UiComponent[AppBrowseControllerProps] = AppBrowseController

  def mapStateToProps(dispatch: Dispatch, state: AdminStateDef, props: Props[Unit]): AppBrowseControllerProps = {
    val applicationsNode = systemGroupController.getApplicationsNode(appsPath)
    
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
      companyController.getCompaniesItem(companiesPath),
      applicationsNode.copy(
        children = systemGroupState.dataList.map { group =>
          val groupNode = systemGroupController.getEnvironmentNode(applicationsNode.path, group)
          val systems = systemState.getSystems(group.id.get)
          groupNode.copy(
            children = systems.map { system =>
              val systemNode = systemController.getApplicationNode(groupNode.path, system)
              val roles = roleState.getRoles(system.id.get)
              val rolesNode = roleController.getRolesNode(BrowsePath(s"${systemNode.path}$rolesPath"))
              systemNode.copy(
                children = List(
                  rolesNode.copy(
                    children = roles.map(roleController.getRoleItem(rolesNode.path, _))
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

object AdminRouteController {

  private val companiesPath = BrowsePath("/companies")
  private val appsPath = BrowsePath("/apps")
  private val rolesPath = BrowsePath("/roles")

  private val groupIdRegex = s"$appsPath/(\\d+)".r
  private val systemIdRegex = s"$appsPath/\\d+/(\\d+)".r
  private val roleIdRegex = s"$appsPath/\\d+/\\d+$rolesPath/(\\d+)".r

  def extractGroupId(path: String): Option[Int] = {
    PathParamsExtractors.extractId(groupIdRegex, path)
  }

  def extractSystemId(path: String, exact: Boolean = false): Option[Int] = {
    PathParamsExtractors.extractId(systemIdRegex, path, exact)
  }
  
  def extractRoleId(path: String): Option[Int] = {
    PathParamsExtractors.extractId(roleIdRegex, path)
  }
}
