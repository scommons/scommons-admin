package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.company.CompanyController
import scommons.admin.client.role.RoleController
import scommons.admin.client.role.permission.RolePermissionController
import scommons.admin.client.system.SystemController
import scommons.admin.client.system.group.SystemGroupController
import scommons.admin.client.system.user.{SystemUserController, SystemUserParams}
import scommons.admin.client.user.{UserController, UserDetailsTab, UserParams}
import scommons.client.app._
import scommons.client.controller.{BaseStateController, PathParams}
import scommons.client.ui.tree._
import scommons.client.ui.Buttons
import scommons.client.util.BrowsePath
import scommons.react.UiComponent

class AdminRouteController(companyController: CompanyController,
                           userController: UserController,
                           systemGroupController: SystemGroupController,
                           systemController: SystemController,
                           systemUserController: SystemUserController,
                           roleController: RoleController,
                           rolePermissionController: RolePermissionController
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
    val userState = state.userState
    val systemGroupState = state.systemGroupState
    val systemState = state.systemState
    val systemUserState = state.systemUserState
    val roleState = state.roleState
    
    List(
      companyController.getCompaniesItem(companiesPath),
      userController.getUsersItem(buildUsersPath(userState.params)),
      applicationsNode.copy(
        children = systemGroupState.dataList.map { group =>
          val groupId = group.id.getOrElse(-1)
          val groupNode = systemGroupController.getEnvironmentNode(applicationsNode.path, group)
          val systems = systemState.getSystems(groupId)
          groupNode.copy(
            children = systems.map { system =>
              val systemId = system.id.getOrElse(-1)
              val systemNode = systemController.getApplicationNode(groupNode.path, system)
              val appsUsersPath = buildAppsUsersPath(systemUserState.params.copy(
                groupId = Some(groupId),
                systemId = Some(systemId)
              ))
              val roles = roleState.getRoles(systemId)
              val rolesNode = roleController.getRolesNode(BrowsePath(s"${systemNode.path}$rolesPath"))
              systemNode.copy(
                children = List(
                  systemUserController.getUsersItem(appsUsersPath, systemId),
                  rolesNode.copy(
                    children = roles.map(roleController.getRoleItem(rolesNode.path, _, rolePermissionController))
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
  private val usersPath = BrowsePath("/users", exact = false)
  private val appsPath = BrowsePath("/apps")
  private val appsUsersPath = BrowsePath("/users")
  private val rolesPath = BrowsePath("/roles")

  private val userIdRegex = s"$usersPath/(\\d+)".r
  private val userTabRegex = s"$usersPath/\\d+/(.+)".r
  
  private val systemGroupIdRegex = s"$appsPath/(\\d+)".r
  private val systemIdRegex = s"$appsPath/\\d+/(\\d+)".r
  private val systemUserIdRegex = s"$appsPath/\\d+/\\d+$appsUsersPath/(\\d+)".r
  private val systemRoleIdRegex = s"$appsPath/\\d+/\\d+$rolesPath/(\\d+)".r

  def buildUsersPath(params: UserParams): BrowsePath = {
    params.userId.map { id =>
      val tabId = params.tab.map(tab => s"/$tab").getOrElse("")

      usersPath.copy(value = s"$usersPath/$id$tabId")
    }.getOrElse(usersPath)
  }
  
  def buildAppsUsersPath(params: SystemUserParams): BrowsePath = {
    val groupId = params.groupId.getOrElse(-1)
    val systemId = params.systemId.getOrElse(-1)
    val basePath = BrowsePath(s"$appsPath/$groupId/$systemId$appsUsersPath", exact = false)
    
    params.userId.map { userId =>
      basePath.copy(value = s"$basePath/$userId")
    }.getOrElse(basePath)
  }
  
  def extractUserId(params: PathParams): Option[Int] =
    params.extractInt(userIdRegex)
  
  def extractUserTab(params: PathParams): Option[UserDetailsTab] =
    params.extract(userTabRegex).flatMap(UserDetailsTab.of)
  
  def extractSystemGroupId(params: PathParams): Option[Int] =
    params.extractInt(systemGroupIdRegex)
  
  def extractSystemId(params: PathParams, exact: Boolean = false): Option[Int] =
    params.extractInt(systemIdRegex, exact)
  
  def extractSystemUserId(params: PathParams): Option[Int] =
    params.extractInt(systemUserIdRegex)
  
  def extractSystemRoleId(params: PathParams): Option[Int] =
    params.extractInt(systemRoleIdRegex)
}
