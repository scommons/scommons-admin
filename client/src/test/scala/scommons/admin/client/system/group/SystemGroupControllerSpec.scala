package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.api.system.SystemListResp
import scommons.admin.client.api.system.group.{SystemGroupData, SystemGroupListResp}
import scommons.admin.client.system.MockSystemActions
import scommons.admin.client.system.SystemActions._
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.admin.client.{AdminImagesCss, MockAdminStateDef}
import scommons.client.controller.RouteParams
import scommons.client.ui.tree.BrowseTreeNodeData
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.BrowsePath
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec

import scala.concurrent.Future
import scala.scalajs.js.Dynamic.literal

class SystemGroupControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class SystemGroupActions {
    val systemGroupListFetch = mockFunction[Dispatch, SystemGroupListFetchAction]

    val actions = new MockSystemGroupActions(
      systemGroupListFetchMock = systemGroupListFetch
    )
  }

  //noinspection TypeAnnotation
  class SystemActions {
    val systemListFetch = mockFunction[Dispatch, SystemListFetchAction]

    val actions = new MockSystemActions(
      systemListFetchMock = systemListFetch
    )
  }

  //noinspection TypeAnnotation
  class State {
    val systemGroupState = mockFunction[SystemGroupState]

    val state = new MockAdminStateDef(
      systemGroupStateMock = systemGroupState
    )
  }

  it should "return component" in {
    //given
    val groupActions = new SystemGroupActions
    val systemActions = new SystemActions
    val controller = new SystemGroupController(groupActions.actions, systemActions.actions)

    //when & then
    controller.uiComponent shouldBe SystemGroupPanel
  }

  it should "map state to props" in {
    //given
    val groupActions = new SystemGroupActions
    val systemActions = new SystemActions
    val controller = new SystemGroupController(groupActions.actions, systemActions.actions)
    val dispatch = mockFunction[Any, Any]
    val systemGroupState = SystemGroupState()
    val state = new State
    val routeParams = new RouteParams(new RouterProps(Props[Unit](literal(
      "location" -> literal("pathname" -> "/apps/123")
    ))))
    
    state.systemGroupState.expects().returning(systemGroupState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, routeParams)

    //then
    inside(result) { case SystemGroupPanelProps(disp, actions, compState, selectedId) =>
      disp shouldBe dispatch
      actions shouldBe groupActions.actions
      compState shouldBe systemGroupState
      selectedId shouldBe Some(123)
    }
  }

  it should "setup applications node" in {
    //given
    val groupActions = new SystemGroupActions
    val systemActions = new SystemActions
    val controller = new SystemGroupController(groupActions.actions, systemActions.actions)
    val systemGroupListFetchAction =
      SystemGroupListFetchAction(FutureTask("Fetching", Future.successful(SystemGroupListResp(Nil))))
    val systemGroupCreateRequestAction = SystemGroupCreateRequestAction(create = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemGroupListFetchAction,
      Buttons.ADD.command -> systemGroupCreateRequestAction
    )
    val appsPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    groupActions.systemGroupListFetch.expects(dispatch)
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

  it should "setup environment node" in {
    //given
    val groupActions = new SystemGroupActions
    val systemActions = new SystemActions
    val controller = new SystemGroupController(groupActions.actions, systemActions.actions)
    val data = SystemGroupData(Some(1), "env 1")
    val systemListFetchAction =
      SystemListFetchAction(FutureTask("Fetching", Future.successful(SystemListResp(Nil))))
    val systemCreateRequestAction = SystemCreateRequestAction(create = true)
    val systemGroupUpdateRequestAction = SystemGroupUpdateRequestAction(update = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemListFetchAction,
      Buttons.ADD.command -> systemCreateRequestAction,
      Buttons.EDIT.command -> systemGroupUpdateRequestAction
    )
    val appsPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    systemActions.systemListFetch.expects(dispatch)
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
