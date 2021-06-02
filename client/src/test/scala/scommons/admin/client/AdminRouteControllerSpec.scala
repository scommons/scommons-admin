package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.company.CompanyController
import scommons.admin.client.role.permission.RolePermissionController
import scommons.admin.client.role.{RoleController, RoleState}
import scommons.admin.client.system.group.{SystemGroupController, SystemGroupState}
import scommons.admin.client.system.user.{SystemUserController, SystemUserParams, SystemUserState}
import scommons.admin.client.system.{SystemController, SystemState}
import scommons.admin.client.user.{UserController, UserDetailsTab, UserParams, UserState}
import scommons.client.app.{AppBrowseController, AppBrowseControllerProps}
import scommons.client.ui.Buttons
import scommons.client.ui.tree.{BrowseTreeItemData, BrowseTreeNodeData}
import scommons.client.util.BrowsePath
import scommons.react.test.TestSpec

class AdminRouteControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val companyController = mock[CompanyController]
    val userController = mock[UserController]
    val systemGroupController = mock[SystemGroupController]
    val systemController = mock[SystemController]
    val systemUserController = mock[SystemUserController]
    val roleController = mock[RoleController]
    val rolePermissionController = mock[RolePermissionController]
    val controller = new AdminRouteController(
      companyController, userController, systemGroupController, systemController, systemUserController,
      roleController, rolePermissionController
    )

    //when & then
    controller.uiComponent shouldBe AppBrowseController
  }
  
  ignore should "map state to props" in {
    //given
    val companyController = mock[CompanyController]
    val userController = mock[UserController]
    val systemGroupController = mock[SystemGroupController]
    val systemController = mock[SystemController]
    val systemUserController = mock[SystemUserController]
    val roleController = mock[RoleController]
    val rolePermissionController = mock[RolePermissionController]
    val controller = new AdminRouteController(
      companyController, userController, systemGroupController, systemController, systemUserController,
      roleController, rolePermissionController
    )
    val props = mock[Props[Unit]]
    val expectedDispatch = mock[Dispatch]
    val userParams = UserParams(Some(123), Some(UserDetailsTab.profile))
    val userState = UserState(userParams)
    val usersPath = buildUsersPath(userParams)
    val systemGroups = List(
      SystemGroupData(Some(1), "env 1"),
      SystemGroupData(Some(2), "env 2")
    )
    val systemGroupState = SystemGroupState(systemGroups)
    val systems = List(
      SystemData(Some(11), "app_1", "", "App 1", "http://app1", 1),
      SystemData(Some(12), "app_2", "", "App 2", "http://app2", 2)
    )
    val systemState = SystemState(systems.groupBy(_.parentId))
    val systemUserParams = SystemUserParams(Some(1), Some(2), Some(3))
    val systemUserState = SystemUserState(systemUserParams)
    val roles = List(
      RoleData(Some(111), 11, "ROLE_1"),
      RoleData(Some(122), 12, "ROLE_2")
    )
    val roleState = RoleState(roles.groupBy(_.systemId))
    val companiesItem = BrowseTreeItemData("Test Companies", BrowsePath("/companies"))
    val usersItem = BrowseTreeItemData("Test Users", usersPath)
    val applicationsNode = BrowseTreeNodeData("Test Applications", BrowsePath("/apps"))
    val environmentNode = BrowseTreeNodeData("Test Env", BrowsePath("/1"))
    val applicationNode = BrowseTreeNodeData("Test App", BrowsePath("/2"))
    val applicationUsersItem = BrowseTreeItemData("Test App Users", BrowsePath("/users"))
    val rolesNode = BrowseTreeNodeData("Test Roles", BrowsePath("/roles"))
    val roleItem = BrowseTreeItemData("Test Role", BrowsePath("/3"))
    (companyController.getCompaniesItem _).expects(companiesItem.path)
      .returning(companiesItem)
    (userController.getUsersItem _).expects(usersItem.path)
      .returning(usersItem)
    (systemGroupController.getApplicationsNode _).expects(BrowsePath("/apps"))
      .returning(applicationsNode)
    systemGroups.foreach { group =>
      (systemGroupController.getEnvironmentNode _).expects(applicationsNode.path, group)
        .returning(environmentNode)
    }
    systems.foreach { system =>
      (systemController.getApplicationNode _).expects(environmentNode.path, system)
        .returning(applicationNode)
      (systemUserController.getUsersItem _).expects(buildAppsUsersPath(systemUserParams.copy(
        groupId = Some(system.parentId),
        systemId = system.id
      )), system.id.get)
        .returning(applicationUsersItem)
      (roleController.getRolesNode _).expects(BrowsePath(s"${applicationNode.path}/roles"))
        .returning(rolesNode)
    }
    roles.foreach { role =>
      (roleController.getRoleItem _).expects(rolesNode.path, role, rolePermissionController)
        .returning(roleItem)
    }
    val expectedTreeRoots = List(
      companiesItem,
      usersItem,
      applicationsNode.copy(
        children = systemGroupState.dataList.map { group =>
          val groupNode = environmentNode
          val systems = systemState.getSystems(group.id.get)
          groupNode.copy(
            children = systems.map { system =>
              val systemNode = applicationNode
              val roles = roleState.getRoles(system.id.get)
              systemNode.copy(
                children = List(
                  applicationUsersItem,
                  rolesNode.copy(
                    children = roles.map(_ => roleItem)
                  )
                )
              )
            }
          )
        }
      )
    )
    val state = mock[AdminStateDef]
    (state.userState _).expects().returning(userState)
    (state.systemGroupState _).expects().returning(systemGroupState)
    (state.systemState _).expects().returning(systemState)
    (state.systemUserState _).expects().returning(systemUserState)
    (state.roleState _).expects().returning(roleState)

    //when
    val result = controller.mapStateToProps(expectedDispatch, state, props)
    
    //then
    inside(result) {
      case AppBrowseControllerProps(
      buttons,
      treeRoots,
      dispatch,
      initiallyOpenedNodes
      ) =>
        buttons shouldBe List(Buttons.REFRESH, Buttons.ADD, Buttons.REMOVE, Buttons.EDIT)
        treeRoots shouldBe expectedTreeRoots
        dispatch shouldBe expectedDispatch
        initiallyOpenedNodes shouldBe Set(applicationsNode.path)
    }
  }
}
