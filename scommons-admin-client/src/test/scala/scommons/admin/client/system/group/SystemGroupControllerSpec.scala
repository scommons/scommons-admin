package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.Location
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminStateDef
import scommons.admin.client.system.group.SystemGroupControllerSpec.LocationMock
import scommons.admin.client.system.group.action.SystemGroupActions
import scommons.client.test.TestSpec

import scala.scalajs.js.annotation.JSExportAll

class SystemGroupControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[SystemGroupActions]
    val controller = new SystemGroupController(apiActions)

    //when & then
    controller.component shouldBe SystemGroupPanel()
  }

  it should "map state to props" in {
    //given
    val apiActions = mock[SystemGroupActions]
    val controller = new SystemGroupController(apiActions)
    val dispatch = mock[Dispatch]
    val systemGroupState = mock[SystemGroupState]
    val state = mock[AdminStateDef]
    val props = mock[Props[Unit]]
    val routerProps = mock[RouterProps]
    val location = mock[LocationMock]
    val pathname = s"${SystemGroupController.path}/123"
    
    (routerProps.location _).expects().returning(location.asInstanceOf[Location])
    (location.pathname _).expects().returning(pathname)
    (state.systemGroupState _).expects().returning(systemGroupState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, props, routerProps)

    //then
    inside(result) { case SystemGroupPanelProps(disp, actions, compState, selectedId) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      compState shouldBe systemGroupState
      selectedId shouldBe Some(123)
    }
  }
}

object SystemGroupControllerSpec {

  @JSExportAll
  trait LocationMock {

    def pathname: String
  }
}
