package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.api.role.{RoleData, RoleListResp}
import scommons.admin.client.role.RoleActions._
import scommons.admin.client.role.RoleControllerSpec._
import scommons.admin.client.role.permission.RolePermissionController
import scommons.admin.client.{AdminImagesCss, MockAdminStateDef}
import scommons.client.controller.RouteParams
import scommons.client.ui.Buttons
import scommons.client.ui.tree.{BrowseTreeItemData, BrowseTreeNodeData}
import scommons.client.util.BrowsePath
import scommons.react._
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec

import scala.concurrent.Future
import scala.scalajs.js.Dynamic.literal

class RoleControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class Actions {
    val roleListFetch = mockFunction[Dispatch, RoleListFetchAction]

    val actions = new MockRoleActions(
      roleListFetchMock = roleListFetch
    )
  }

  //noinspection TypeAnnotation
  class State {
    val roleState = mockFunction[RoleState]

    val state = new MockAdminStateDef(
      roleStateMock = roleState
    )
  }

  it should "return component" in {
    //given
    val apiActions = mock[RoleActions]
    val controller = new RoleController(apiActions)

    //when & then
    controller.uiComponent shouldBe RolePanel
  }

  it should "map state to props" in {
    //given
    val apiActions = mock[RoleActions]
    val controller = new RoleController(apiActions)
    val dispatch = mock[Dispatch]
    val roleState = mock[RoleState]
    val state = new State
    val routeParams = new RouteParams(new RouterProps(Props[Unit](literal(
      "location" -> literal("pathname" -> "/apps/1/2/roles/3")
    ))))
    
    state.roleState.expects().returning(roleState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, routeParams)

    //then
    inside(result) { case RolePanelProps(disp, actions, compState, selectedSystemId, selectedId) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      compState shouldBe roleState
      selectedSystemId shouldBe Some(2)
      selectedId shouldBe Some(3)
    }
  }

  it should "setup roles node" in {
    //given
    val actions = new Actions
    val controller = new RoleController(actions.actions)
    val rolesPath = BrowsePath("/some-path")
    val roleListFetchAction =
      RoleListFetchAction(FutureTask("Fetching", Future.successful(RoleListResp(Nil))))
    val roleCreateRequestAction = RoleCreateRequestAction(create = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> roleListFetchAction,
      Buttons.ADD.command -> roleCreateRequestAction
    )
    val dispatch = mockFunction[Any, Any]

    actions.roleListFetch.expects(dispatch).returning(roleListFetchAction)
    dispatch.expects(roleListFetchAction).returning(*)
    dispatch.expects(roleCreateRequestAction).returning(*)

    //when
    val result = controller.getRolesNode(rolesPath)

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
        path shouldBe rolesPath
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
    val apiActions = mock[RoleActions]
    val controller = new RoleController(apiActions)
    val rolesPath = BrowsePath("/some-path")
    val data = RoleData(
      id = Some(111),
      systemId = 11,
      title = "ROLE_1"
    )
    val roleUpdateRequestAction = RoleUpdateRequestAction(update = true)
    val expectedActions = Map(
      Buttons.EDIT.command -> roleUpdateRequestAction
    )
    val roleControllerReactClass = new FunctionComponent[Unit] {
      protected def render(props: Props): ReactElement = {
        <.div()("test")
      }
    }.apply()
    val rolePermissionController = new TestRolePermissionController(roleControllerReactClass)
    
    val dispatch = mockFunction[Any, Any]
    dispatch.expects(roleUpdateRequestAction).returning(*)

    //when
    val result = controller.getRoleItem(rolesPath, data, rolePermissionController)

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
        reactClass shouldBe Some(roleControllerReactClass)
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
}

object RoleControllerSpec {

  class TestRolePermissionController(reactClass: ReactClass)
    extends RolePermissionController(null) {

    override def apply(): ReactClass = reactClass
  }
}
