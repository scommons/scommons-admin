package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.action.ApiActions
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.company.action.CompanyListFetchAction
import scommons.admin.client.system.group.SystemGroupState
import scommons.admin.client.system.group.action._
import scommons.client.app.{AppBrowseController, AppBrowseControllerProps}
import scommons.client.test.TestSpec
import scommons.client.ui.tree.{BrowseTreeItemData, BrowseTreeNodeData}
import scommons.client.ui.{ButtonImagesCss, Buttons}

class AdminRouteControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[ApiActions]
    val controller = new AdminRouteController(apiActions)

    //when & then
    controller.component shouldBe AppBrowseController()
  }
  
  it should "map state to props" in {
    //given
    val apiActions = mock[ApiActions]
    val controller = new AdminRouteController(apiActions)
    val props = mock[Props[Unit]]
    val expectedDispatch = mock[Dispatch]
    val systemGroupState = SystemGroupState(List(
      SystemGroupData(Some(1), "env 1"),
      SystemGroupData(Some(2), "env 2")
    ))
    val expectedTreeRoots = List(
      controller.companiesItem,
      controller.environmentsNode.copy(
        children = systemGroupState.dataList.map(controller.getEnvironmentItem)
      )
    )
    val state = mock[AdminStateDef]
    (state.systemGroupState _).expects()
      .returning(systemGroupState)

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
        initiallyOpenedNodes shouldBe Set(controller.environmentsNode.path)
    }
  }

  it should "setup companies item" in {
    //given
    val apiActions = mock[ApiActions]
    val controller = new AdminRouteController(apiActions)
    val companyListFetchAction = mock[CompanyListFetchAction]
    val dispatch = mockFunction[Any, Any]

    (apiActions.companyListFetch _).expects(dispatch, None, None)
      .returning(companyListFetchAction)
    dispatch.expects(companyListFetchAction)
      .returning(*)

    //when
    val result = controller.companiesItem

    //then
    inside(result) {
      case BrowseTreeItemData(
      text,
      path,
      image,
      actions,
      reactClass
      ) =>
        text shouldBe "Companies"
        path.value shouldBe "/companies"
        image shouldBe Some(ButtonImagesCss.folder)
        reactClass should not be None
        actions.enabledCommands shouldBe Set(Buttons.REFRESH.command)
        actions.onCommand(dispatch)(Buttons.REFRESH.command) shouldBe companyListFetchAction
    }
  }

  it should "setup environments node" in {
    //given
    val apiActions = mock[ApiActions]
    val controller = new AdminRouteController(apiActions)
    val systemGroupListFetchAction = mock[SystemGroupListFetchAction]
    val systemGroupCreateRequestAction = SystemGroupCreateRequestAction(create = true)
    val dispatch = mockFunction[Any, Any]

    (apiActions.systemGroupListFetch _).expects(dispatch)
      .returning(systemGroupListFetchAction)
    dispatch.expects(systemGroupListFetchAction).returning(*)
    dispatch.expects(systemGroupCreateRequestAction).returning(*)

    //when
    val result = controller.environmentsNode

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
        text shouldBe "Environments"
        path.value shouldBe "/environments"
        image shouldBe Some(AdminImagesCss.computer)
        reactClass shouldBe None
        actions.enabledCommands shouldBe Set(Buttons.REFRESH.command, Buttons.ADD.command)
        actions.onCommand(dispatch)(Buttons.REFRESH.command) shouldBe systemGroupListFetchAction
        actions.onCommand(dispatch)(Buttons.ADD.command) shouldBe systemGroupCreateRequestAction
    }
  }

  it should "setup environment item" in {
    //given
    val apiActions = mock[ApiActions]
    val controller = new AdminRouteController(apiActions)
    val data = SystemGroupData(Some(1), "env 1")
    val systemGroupUpdateRequestAction = SystemGroupUpdateRequestAction(update = true)
    val dispatch = mockFunction[Any, Any]
    
    dispatch.expects(systemGroupUpdateRequestAction).returning(*)

    //when
    val result = controller.getEnvironmentItem(data)

    //then
    inside(result) {
      case BrowseTreeItemData(
      text,
      path,
      image,
      actions,
      reactClass
      ) =>
        text shouldBe data.name
        path.value shouldBe s"/environments/${data.id.get}"
        image shouldBe Some(ButtonImagesCss.folder)
        reactClass shouldBe None
        actions.enabledCommands shouldBe Set(Buttons.EDIT.command)
        actions.onCommand(dispatch)(Buttons.EDIT.command) shouldBe systemGroupUpdateRequestAction
    }
  }
}
