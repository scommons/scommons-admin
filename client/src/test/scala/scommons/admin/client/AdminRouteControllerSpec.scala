package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import scommons.admin.client.AdminRouteController._
import scommons.admin.client.AdminRouteControllerSpec._
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
import scommons.react.redux.Dispatch
import scommons.react.test.TestSpec

class AdminRouteControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class State {
    val userState = mockFunction[UserState]
    val systemGroupState = mockFunction[SystemGroupState]
    val systemState = mockFunction[SystemState]
    val systemUserState = mockFunction[SystemUserState]
    val roleState = mockFunction[RoleState]

    val state = new MockAdminStateDef(
      userStateMock = userState,
      systemGroupStateMock = systemGroupState,
      systemStateMock = systemState,
      systemUserStateMock = systemUserState,
      roleStateMock = roleState
    )
  }

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
  
  it should "map state to props" in {
    //given
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
    val companyController = new TestCompanyController(companiesItem)(fail(_))
    val userController = new TestUserController(usersItem)(fail(_))
    val systemGroupController = new TestSystemGroupController(applicationsNode, environmentNode, systemGroups)(fail(_))
    val systemController = new TestSystemController(environmentNode.path, applicationNode, systems)(fail(_))
    val systemUserController = new TestSystemUserController(systemUserParams, applicationUsersItem, systems)(fail(_))
    val rolePermissionController = mock[RolePermissionController]
    val roleController = new TestRoleController(
      rolesPath = BrowsePath(s"${applicationNode.path}/roles"),
      rolesNode = rolesNode,
      roleItem = roleItem,
      expectedRolePermissionController = rolePermissionController,
      roles = roles
    )(fail(_))
    val controller = new AdminRouteController(
      companyController, userController, systemGroupController, systemController, systemUserController,
      roleController, rolePermissionController
    )
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
    val state = new State
    state.userState.expects().returning(userState)
    state.systemGroupState.expects().returning(systemGroupState)
    state.systemState.expects().returning(systemState)
    state.systemUserState.expects().returning(systemUserState)
    state.roleState.expects().returning(roleState)

    //when
    val result = controller.mapStateToProps(expectedDispatch, state.state, props)
    
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

object AdminRouteControllerSpec {
  
  class TestCompanyController(itemData: BrowseTreeItemData)(fail: String => Nothing)
    extends CompanyController(null) {

    override def getCompaniesItem(path: BrowsePath): BrowseTreeItemData = {
      if (path != itemData.path) {
        fail(s"companies path doesn't match: expected ${itemData.path}, but got: $path")
      }
      itemData
    }
  }

  class TestUserController(itemData: BrowseTreeItemData)(fail: String => Nothing)
    extends UserController(null, null, null) {

    override def getUsersItem(path: BrowsePath): BrowseTreeItemData = {
      if (path != itemData.path) {
        fail(s"users path doesn't match: expected ${itemData.path}, but got: $path")
      }
      itemData
    }
  }

  class TestSystemGroupController(
                                   applicationsNode: BrowseTreeNodeData,
                                   environmentNode: BrowseTreeNodeData,
                                   systemGroups: List[SystemGroupData]
                                 )(fail: String => Nothing)
    extends SystemGroupController(null, null) {

    override def getApplicationsNode(path: BrowsePath): BrowseTreeNodeData = {
      val expectedPath = BrowsePath("/apps")
      if (path != expectedPath) {
        fail(s"applications path doesn't match: expected $expectedPath, but got: $path")
      }
      applicationsNode
    }

    override def getEnvironmentNode(path: BrowsePath, data: SystemGroupData): BrowseTreeNodeData = {
      if (path != applicationsNode.path) {
        fail(s"environment path doesn't match: expected ${applicationsNode.path}, but got: $path")
      }
      systemGroups.find(_ == data).getOrElse {
        fail(s"cannot find SystemGroupData: $data")
      }
      environmentNode
    }
  }

  class TestSystemController(
                              envNodePath: BrowsePath,
                              applicationNode: BrowseTreeNodeData,
                              systems: List[SystemData]
                            )(fail: String => Nothing)
    extends SystemController(null) {

    override def getApplicationNode(path: BrowsePath, data: SystemData): BrowseTreeNodeData = {
      if (path != envNodePath) {
        fail(s"system path doesn't match: expected $envNodePath, but got: $path")
      }
      systems.find(_ == data).getOrElse {
        fail(s"cannot find SystemData: $data")
      }
      applicationNode
    }
  }

  class TestSystemUserController(
                                  systemUserParams: SystemUserParams,
                                  applicationUsersItem: BrowseTreeItemData,
                                  systems: List[SystemData]
                                )(fail: String => Nothing)
    extends SystemUserController(null) {

    override def getUsersItem(path: BrowsePath, systemId: Int): BrowseTreeItemData = {
      val system = systems.find(_.id.get == systemId).getOrElse {
        fail(s"cannot find SystemData, systemId: $systemId")
      }
      val expectedPath = buildAppsUsersPath(systemUserParams.copy(
        groupId = Some(system.parentId),
        systemId = system.id
      ))
      if (path != expectedPath) {
        fail(s"system users path doesn't match: expected $expectedPath, but got: $path")
      }
      applicationUsersItem
    }
  }

  class TestRoleController(
                            rolesPath: BrowsePath,
                            rolesNode: BrowseTreeNodeData,
                            roleItem: BrowseTreeItemData,
                            expectedRolePermissionController: RolePermissionController,
                            roles: List[RoleData]
                          )(fail: String => Nothing)
    extends RoleController(null) {

    override def getRolesNode(path: BrowsePath): BrowseTreeNodeData = {
      if (path != rolesPath) {
        fail(s"system users path doesn't match: expected $rolesPath, but got: $path")
      }
      rolesNode
    }

    override def getRoleItem(path: BrowsePath,
                             data: RoleData,
                             rolePermissionController: RolePermissionController): BrowseTreeItemData = {
      
      if (path != rolesNode.path) {
        fail(s"role path doesn't match: expected ${rolesNode.path}, but got: $path")
      }
      roles.find(_ == data).getOrElse {
        fail(s"cannot find RoleData: $data")
      }
      if (rolePermissionController != expectedRolePermissionController) {
        fail(
          s"""rolePermissionController doesn't match:
             |  expected $expectedRolePermissionController
             |  actual: $rolePermissionController
             |""".stripMargin)
      }
      roleItem
    }
  }
}
