package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.SystemActions._
import scommons.admin.client.{AdminImagesCss, MockAdminStateDef}
import scommons.client.controller.RouteParams
import scommons.client.ui.Buttons
import scommons.client.ui.tree.BrowseTreeNodeData
import scommons.client.util.BrowsePath
import scommons.react.redux.Dispatch
import scommons.react.test.TestSpec

import scala.scalajs.js.Dynamic.literal

class SystemControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class State {
    val systemState = mockFunction[SystemState]

    val state = new MockAdminStateDef(
      systemStateMock = systemState
    )
  }

  it should "return component" in {
    //given
    val actions = new MockSystemActions
    val controller = new SystemController(actions)

    //when & then
    controller.uiComponent shouldBe SystemPanel
  }

  it should "map state to props" in {
    //given
    val actions = new MockSystemActions
    val controller = new SystemController(actions)
    val dispatch = mock[Dispatch]
    val systemState = mock[SystemState]
    val state = new State
    val routeParams = new RouteParams(new RouterProps(Props[Unit](literal(
      "location" -> literal("pathname" -> "/apps/123/456")
    ))))
    
    state.systemState.expects().returning(systemState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, routeParams)

    //then
    inside(result) { case SystemPanelProps(disp, resActions, compState, selectedParentId, selectedId) =>
      disp shouldBe dispatch
      resActions shouldBe actions
      compState shouldBe systemState
      selectedParentId shouldBe Some(123)
      selectedId shouldBe Some(456)
    }
  }
  
  it should "not map selected system id if path is not exact" in {
    //given
    val actions = new MockSystemActions
    val controller = new SystemController(actions)
    val dispatch = mock[Dispatch]
    val systemState = mock[SystemState]
    val state = new State
    val routeParams = new RouteParams(new RouterProps(Props[Unit](literal(
      "location" -> literal("pathname" -> "/apps/123/456/not-exact")
    ))))
    
    state.systemState.expects().returning(systemState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, routeParams)

    //then
    inside(result) { case SystemPanelProps(disp, resActions, compState, selectedParentId, selectedId) =>
      disp shouldBe dispatch
      resActions shouldBe actions
      compState shouldBe systemState
      selectedParentId shouldBe Some(123)
      selectedId shouldBe None
    }
  }

  it should "setup application node" in {
    //given
    val actions = new MockSystemActions
    val controller = new SystemController(actions)
    val parentId = 1
    val parentPath = BrowsePath(s"/apps/$parentId")
    val data = SystemData(
      id = Some(11),
      name = "app_1",
      password = "",
      title = "App 1",
      url = "http://app1",
      parentId = parentId
    )
    val systemUpdateRequestAction = SystemUpdateRequestAction(update = true)
    val expectedActions = Map(
      Buttons.EDIT.command -> systemUpdateRequestAction
    )
    val dispatch = mockFunction[Any, Any]

    dispatch.expects(systemUpdateRequestAction).returning(*)

    //when
    val result = controller.getApplicationNode(parentPath, data)

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
}
