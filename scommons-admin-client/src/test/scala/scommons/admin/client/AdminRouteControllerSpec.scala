package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.company.CompanyController
import scommons.admin.client.role.RoleActions._
import scommons.admin.client.role.{RoleController, RoleState}
import scommons.admin.client.system.group.{SystemGroupController, SystemGroupState}
import scommons.admin.client.system.{SystemController, SystemState}
import scommons.client.app.{AppBrowseController, AppBrowseControllerProps}
import scommons.client.test.TestSpec
import scommons.client.ui.Buttons
import scommons.client.ui.tree.{BrowseTreeItemData, BrowseTreeNodeData}
import scommons.client.util.BrowsePath

class AdminRouteControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val systemGroupController = mock[SystemGroupController]
    val systemController = mock[SystemController]
    val controller = new AdminRouteController(apiActions, companyController, systemGroupController, systemController)

    //when & then
    controller.component shouldBe AppBrowseController()
  }
  
  it should "map state to props" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val systemGroupController = mock[SystemGroupController]
    val systemController = mock[SystemController]
    val controller = new AdminRouteController(apiActions, companyController, systemGroupController, systemController)
    val props = mock[Props[Unit]]
    val expectedDispatch = mock[Dispatch]
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
    val roleState = RoleState(Map(
      11 -> List(RoleData(Some(111), 11, "ROLE_1")),
      12 -> List(RoleData(Some(122), 12, "ROLE_2"))
    ))
    val companiesItem = BrowseTreeItemData("Test Companies", BrowsePath("/companies"))
    (companyController.getCompaniesItem _).expects().returning(companiesItem)
    val applicationsNode = BrowseTreeNodeData("Test Applications", BrowsePath("/apps"))
    val environmentNode = BrowseTreeNodeData("Test Env", BrowsePath("/env"))
    val applicationNode = BrowseTreeNodeData("Test App", BrowsePath("/app"))
    (systemGroupController.getApplicationsNode _).expects().returning(applicationsNode)
    systemGroups.foreach { group =>
      (systemGroupController.getEnvironmentNode _).expects(group)
        .returning(environmentNode)
    }
    systems.foreach { system =>
      (systemController.getApplicationNode _).expects(environmentNode.path.value, system)
        .returning(applicationNode)
    }
    val expectedTreeRoots = List(
      companiesItem,
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
                  controller.getRolesNode(systemNode.path.value, system, roles)
                )
              )
            }
          )
        }
      )
    )
    val state = mock[AdminStateDef]
    (state.systemGroupState _).expects().returning(systemGroupState)
    (state.systemState _).expects().returning(systemState)
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

  it should "setup roles node" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val systemGroupController = mock[SystemGroupController]
    val systemController = mock[SystemController]
    val controller = new AdminRouteController(apiActions, companyController, systemGroupController, systemController)
    val parentId = 1
    val parentPath = s"${SystemGroupController.path}/$parentId"
    val data = SystemData(
      id = Some(11),
      name = "app_1",
      password = "",
      title = "App 1",
      url = "http://app1",
      parentId = parentId
    )
    val appPath = s"$parentPath/${data.id.get}"
    val roles = List(
      RoleData(Some(111), 11, "ROLE_1")
    )
    val roleListFetchAction = mock[RoleListFetchAction]
    val roleCreateRequestAction = RoleCreateRequestAction(create = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> roleListFetchAction,
      Buttons.ADD.command -> roleCreateRequestAction
    )
    val dispatch = mockFunction[Any, Any]

    (apiActions.roleListFetch _).expects(dispatch)
      .returning(roleListFetchAction)
    dispatch.expects(roleListFetchAction).returning(*)
    dispatch.expects(roleCreateRequestAction).returning(*)

    //when
    val result = controller.getRolesNode(appPath, data, roles)

    //then
    inside(result) {
      case BrowseTreeNodeData(
      text,
      path,
      image,
      actions,
      reactClass,
      _
      ) =>
        text shouldBe "Roles"
        path.value shouldBe s"$appPath/${RoleController.pathName}"
        image shouldBe Some(AdminImagesCss.role)
        reactClass shouldBe None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
  
  it should "setup role item" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val systemGroupController = mock[SystemGroupController]
    val systemController = mock[SystemController]
    val controller = new AdminRouteController(apiActions, companyController, systemGroupController, systemController)
    val rolesPath = "/some-path"
    val data = RoleData(
      id = Some(111),
      systemId = 11,
      title = "ROLE_1"
    )
    val roleUpdateRequestAction = RoleUpdateRequestAction(update = true)
    val expectedActions = Map(
      Buttons.EDIT.command -> roleUpdateRequestAction
    )
    val dispatch = mockFunction[Any, Any]

    dispatch.expects(roleUpdateRequestAction).returning(*)

    //when
    val result = controller.getRoleItem(rolesPath, data)

    //then
    inside(result) {
      case BrowseTreeItemData(
      text,
      path,
      image,
      actions,
      reactClass
      ) =>
        text shouldBe data.title
        path.value shouldBe s"$rolesPath/${data.id.get}"
        image shouldBe Some(AdminImagesCss.role)
        reactClass shouldBe None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
}
