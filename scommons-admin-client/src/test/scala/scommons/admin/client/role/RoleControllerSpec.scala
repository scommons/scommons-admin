package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.Location
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminStateDef
import scommons.admin.client.role.RoleControllerSpec.LocationMock
import scommons.admin.client.system.group.SystemGroupController
import scommons.client.test.TestSpec

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
    val pathname = s"${SystemGroupController.path}/1/2/roles/3"
    
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
}

object RoleControllerSpec {

  @JSExportAll
  trait LocationMock {

    def pathname: String
  }
}
