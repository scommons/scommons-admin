package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.Location
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.role.RoleActions._
import scommons.admin.client.role.RoleControllerSpec.LocationMock
import scommons.admin.client.{AdminImagesCss, AdminRouteController, AdminStateDef}
import scommons.client.test.TestSpec
import scommons.client.ui.Buttons
import scommons.client.ui.tree.{BrowseTreeItemData, BrowseTreeNodeData}

import scala.scalajs.js.annotation.JSExportAll

class RoleControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[RoleActions]
    val controller = new RoleController(apiActions)

    //when & then
    controller.component shouldBe RolePanel()
  }

  it should "map state to props" in {
    //given
    val apiActions = mock[RoleActions]
    val controller = new RoleController(apiActions)
    val dispatch = mock[Dispatch]
    val roleState = mock[RoleState]
    val state = mock[AdminStateDef]
    val props = mock[Props[Unit]]
    val routerProps = mock[RouterProps]
    val location = mock[LocationMock]
    val pathname = s"${AdminRouteController.appsPath}/1/2/roles/3"
    
    (routerProps.location _).expects().returning(location.asInstanceOf[Location])
    (location.pathname _).expects().returning(pathname)
    (state.roleState _).expects().returning(roleState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, props, routerProps)

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
    val apiActions = mock[RoleActions]
    val controller = new RoleController(apiActions)
    val appPath = "/some-path"
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
    val result = controller.getRolesNode(appPath)

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
        path.value shouldBe s"$appPath/${AdminRouteController.rolesPath}"
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

object RoleControllerSpec {

  @JSExportAll
  trait LocationMock {

    def pathname: String
  }
}
