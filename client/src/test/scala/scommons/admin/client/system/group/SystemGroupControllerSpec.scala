package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.system.SystemActions
import scommons.admin.client.system.SystemActions._
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{PathParams, RouteParams}
import scommons.client.ui.tree.BrowseTreeNodeData
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.BrowsePath
import scommons.react.test.TestSpec

class SystemGroupControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val groupActions = mock[SystemGroupActions]
    val systemActions = mock[SystemActions]
    val controller = new SystemGroupController(groupActions, systemActions)

    //when & then
    controller.uiComponent shouldBe SystemGroupPanel
  }

  it should "map state to props" in {
    //given
    val groupActions = mock[SystemGroupActions]
    val systemActions = mock[SystemActions]
    val controller = new SystemGroupController(groupActions, systemActions)
    val dispatch = mock[Dispatch]
    val systemGroupState = mock[SystemGroupState]
    val state = mock[AdminStateDef]
    val routeParams = mock[RouteParams]
    val pathParams = PathParams("/apps/123")
    
    (routeParams.pathParams _).expects().returning(pathParams)
    (state.systemGroupState _).expects().returning(systemGroupState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, routeParams)

    //then
    inside(result) { case SystemGroupPanelProps(disp, actions, compState, selectedId) =>
      disp shouldBe dispatch
      actions shouldBe groupActions
      compState shouldBe systemGroupState
      selectedId shouldBe Some(123)
    }
  }

  ignore should "setup applications node" in {
    //given
    val groupActions = mock[SystemGroupActions]
    val systemActions = mock[SystemActions]
    val controller = new SystemGroupController(groupActions, systemActions)
    val systemGroupListFetchAction = mock[SystemGroupListFetchAction]
    val systemGroupCreateRequestAction = SystemGroupCreateRequestAction(create = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemGroupListFetchAction,
      Buttons.ADD.command -> systemGroupCreateRequestAction
    )
    val appsPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    (groupActions.systemGroupListFetch _).expects(dispatch)
      .returning(systemGroupListFetchAction)
    dispatch.expects(systemGroupListFetchAction).returning(*)
    dispatch.expects(systemGroupCreateRequestAction).returning(*)

    //when
    val result = controller.getApplicationsNode(appsPath)

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
        path shouldBe appsPath
        image shouldBe Some(AdminImagesCss.computer)
        reactClass shouldBe None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }

  ignore should "setup environment node" in {
    //given
    val groupActions = mock[SystemGroupActions]
    val systemActions = mock[SystemActions]
    val controller = new SystemGroupController(groupActions, systemActions)
    val data = SystemGroupData(Some(1), "env 1")
    val systemListFetchAction = mock[SystemListFetchAction]
    val systemCreateRequestAction = SystemCreateRequestAction(create = true)
    val systemGroupUpdateRequestAction = SystemGroupUpdateRequestAction(update = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemListFetchAction,
      Buttons.ADD.command -> systemCreateRequestAction,
      Buttons.EDIT.command -> systemGroupUpdateRequestAction
    )
    val appsPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    (systemActions.systemListFetch _).expects(dispatch)
      .returning(systemListFetchAction)
    dispatch.expects(systemListFetchAction).returning(*)
    dispatch.expects(systemCreateRequestAction).returning(*)
    dispatch.expects(systemGroupUpdateRequestAction).returning(*)

    //when
    val result = controller.getEnvironmentNode(appsPath, data)

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
        path.value shouldBe s"$appsPath/${data.id.get}"
        image shouldBe Some(ButtonImagesCss.folder)
        reactClass shouldBe None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
}
