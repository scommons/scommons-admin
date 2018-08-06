package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.company.CompanyController
import scommons.admin.client.role.RoleActions._
import scommons.admin.client.role.{RoleController, RoleState}
import scommons.admin.client.system.SystemActions._
import scommons.admin.client.system.SystemState
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.admin.client.system.group.{SystemGroupController, SystemGroupState}
import scommons.client.app.{AppBrowseController, AppBrowseControllerProps}
import scommons.client.test.TestSpec
import scommons.client.ui.tree.{BrowseTreeItemData, BrowseTreeNodeData}
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.BrowsePath

class AdminRouteControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val controller = new AdminRouteController(apiActions, companyController)

    //when & then
    controller.component shouldBe AppBrowseController()
  }
  
  it should "map state to props" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val controller = new AdminRouteController(apiActions, companyController)
    val props = mock[Props[Unit]]
    val expectedDispatch = mock[Dispatch]
    val systemGroupState = SystemGroupState(List(
      SystemGroupData(Some(1), "env 1"),
      SystemGroupData(Some(2), "env 2")
    ))
    val systemState = SystemState(Map(
      1 -> List(SystemData(Some(11), "app_1", "", "App 1", "http://app1", 1)),
      2 -> List(SystemData(Some(12), "app_2", "", "App 2", "http://app2", 2))
    ))
    val roleState = RoleState(Map(
      11 -> List(RoleData(Some(111), 11, "ROLE_1")),
      12 -> List(RoleData(Some(122), 12, "ROLE_2"))
    ))
    val companiesItem = BrowseTreeItemData("Test", BrowsePath("/"))
    (companyController.getCompaniesItem _).expects().returning(companiesItem)
    val expectedTreeRoots = List(
      companiesItem,
      controller.applicationsNode.copy(
        children = systemGroupState.dataList.map { group =>
          controller.getEnvironmentNode(
            group,
            systemState.systemsByParentId.getOrElse(group.id.get, Nil),
            roleState
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
        initiallyOpenedNodes shouldBe Set(controller.applicationsNode.path)
    }
  }

  it should "setup applications node" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val controller = new AdminRouteController(apiActions, companyController)
    val systemGroupListFetchAction = mock[SystemGroupListFetchAction]
    val systemGroupCreateRequestAction = SystemGroupCreateRequestAction(create = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemGroupListFetchAction,
      Buttons.ADD.command -> systemGroupCreateRequestAction
    )
    val dispatch = mockFunction[Any, Any]

    (apiActions.systemGroupListFetch _).expects(dispatch)
      .returning(systemGroupListFetchAction)
    dispatch.expects(systemGroupListFetchAction).returning(*)
    dispatch.expects(systemGroupCreateRequestAction).returning(*)

    //when
    val result = controller.applicationsNode

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
        text shouldBe "Applications"
        path.value shouldBe SystemGroupController.path
        image shouldBe Some(AdminImagesCss.computer)
        reactClass shouldBe None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }

  it should "setup environment node" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val controller = new AdminRouteController(apiActions, companyController)
    val data = SystemGroupData(Some(1), "env 1")
    val systems = List(
      SystemData(Some(11), "app_1", "", "App 1", "http://app1", 1)
    )
    val roles = List(
      RoleData(Some(111), 11, "ROLE_1")
    )
    val systemListFetchAction = mock[SystemListFetchAction]
    val systemCreateRequestAction = SystemCreateRequestAction(create = true)
    val systemGroupUpdateRequestAction = SystemGroupUpdateRequestAction(update = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemListFetchAction,
      Buttons.ADD.command -> systemCreateRequestAction,
      Buttons.EDIT.command -> systemGroupUpdateRequestAction
    )
    val dispatch = mockFunction[Any, Any]

    (apiActions.systemListFetch _).expects(dispatch)
      .returning(systemListFetchAction)
    dispatch.expects(systemListFetchAction).returning(*)
    dispatch.expects(systemCreateRequestAction).returning(*)
    dispatch.expects(systemGroupUpdateRequestAction).returning(*)

    //when
    val result = controller.getEnvironmentNode(data, systems, RoleState(roles.groupBy(_.systemId)))

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
        text shouldBe data.name
        path.value shouldBe s"${SystemGroupController.path}/${data.id.get}"
        image shouldBe Some(ButtonImagesCss.folder)
        reactClass shouldBe None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
  
  it should "setup application node" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val controller = new AdminRouteController(apiActions, companyController)
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
    val roles = List(
      RoleData(Some(111), 11, "ROLE_1")
    )
    val systemUpdateRequestAction = SystemUpdateRequestAction(update = true)
    val expectedActions = Map(
      Buttons.EDIT.command -> systemUpdateRequestAction
    )
    val dispatch = mockFunction[Any, Any]

    dispatch.expects(systemUpdateRequestAction).returning(*)

    //when
    val result = controller.getApplicationNode(parentPath, data, roles)

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
        text shouldBe data.name
        path.value shouldBe s"$parentPath/${data.id.get}"
        image shouldBe Some(AdminImagesCss.computer)
        reactClass shouldBe None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
  
  it should "setup roles node" in {
    //given
    val apiActions = mock[AdminActions]
    val companyController = mock[CompanyController]
    val controller = new AdminRouteController(apiActions, companyController)
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
    val controller = new AdminRouteController(apiActions, companyController)
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
