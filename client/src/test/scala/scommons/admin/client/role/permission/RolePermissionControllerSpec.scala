package scommons.admin.client.role.permission

import scommons.admin.client.AdminStateDef
import scommons.client.controller.{PathParams, RouteParams}
import scommons.react.redux.Dispatch
import scommons.react.test.TestSpec

class RolePermissionControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[RolePermissionActions]
    val controller = new RolePermissionController(apiActions)

    //when & then
    controller.uiComponent shouldBe RolePermissionPanel
  }

  it should "map state to props" in {
    //given
    val apiActions = mock[RolePermissionActions]
    val controller = new RolePermissionController(apiActions)
    val dispatch = mock[Dispatch]
    val rolePermissionState = mock[RolePermissionState]
    val state = mock[AdminStateDef]
    val routeParams = mock[RouteParams]
    val pathParams = PathParams("/apps/1/2/roles/3")

    (routeParams.pathParams _).expects().returning(pathParams)
    (state.rolePermissionState _).expects().returning(rolePermissionState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, routeParams)

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
    val apiActions = mock[RolePermissionActions]
    val controller = new RolePermissionController(apiActions)
    val dispatch = mock[Dispatch]
    val rolePermissionState = mock[RolePermissionState]
    val state = mock[AdminStateDef]
    val routeParams = mock[RouteParams]
    val pathParams = PathParams("/apps/1/2/roles")

    (routeParams.pathParams _).expects().returning(pathParams)
    (state.rolePermissionState _).expects().returning(rolePermissionState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, routeParams)

    //then
    inside(result) { case RolePermissionPanelProps(disp, actions, compState, selectedRoleId) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      compState shouldBe rolePermissionState
      selectedRoleId shouldBe -1
    }
  }
}
