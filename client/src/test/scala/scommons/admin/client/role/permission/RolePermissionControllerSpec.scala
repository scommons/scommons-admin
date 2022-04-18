package scommons.admin.client.role.permission

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.MockAdminStateDef
import scommons.client.controller.RouteParams
import scommons.react.test.TestSpec

import scala.scalajs.js.Dynamic.literal

class RolePermissionControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class State {
    val rolePermissionState = mockFunction[RolePermissionState]

    val state = new MockAdminStateDef(
      rolePermissionStateMock = rolePermissionState
    )
  }

  it should "return component" in {
    //given
    val apiActions = new MockRolePermissionActions
    val controller = new RolePermissionController(apiActions)

    //when & then
    controller.uiComponent shouldBe RolePermissionPanel
  }

  it should "map state to props" in {
    //given
    val apiActions = new MockRolePermissionActions
    val controller = new RolePermissionController(apiActions)
    val dispatch = mockFunction[Any, Any]
    val rolePermissionState = RolePermissionState()
    val state = new State
    val routeParams = new RouteParams(new RouterProps(Props[Unit](literal(
      "location" -> literal("pathname" -> "/apps/1/2/roles/3")
    ))))
    
    state.rolePermissionState.expects().returning(rolePermissionState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, routeParams)

    //then
    inside(result) { case RolePermissionPanelProps(disp, actions, compState, selectedRoleId) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      compState shouldBe rolePermissionState
      selectedRoleId shouldBe 3
    }
  }
  
  it should "set selectedRoleId to -1 if no such id when mapStateAndRouteToProps" in {
    //given
    val apiActions = new MockRolePermissionActions
    val controller = new RolePermissionController(apiActions)
    val dispatch = mockFunction[Any, Any]
    val rolePermissionState = RolePermissionState()
    val state = new State
    val routeParams = new RouteParams(new RouterProps(Props[Unit](literal(
      "location" -> literal("pathname" -> "/apps/1/2/roles")
    ))))

    state.rolePermissionState.expects().returning(rolePermissionState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, routeParams)

    //then
    inside(result) { case RolePermissionPanelProps(disp, actions, compState, selectedRoleId) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      compState shouldBe rolePermissionState
      selectedRoleId shouldBe -1
    }
  }
}
