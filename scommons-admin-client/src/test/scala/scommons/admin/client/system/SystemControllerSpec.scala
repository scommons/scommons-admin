package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.Location
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminStateDef
import scommons.admin.client.system.SystemControllerSpec.LocationMock
import scommons.admin.client.system.group.SystemGroupController
import scommons.client.test.TestSpec

import scala.scalajs.js.annotation.JSExportAll

class SystemControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[SystemActions]
    val controller = new SystemController(apiActions)

    //when & then
    controller.component shouldBe SystemPanel()
  }

  it should "map state to props" in {
    //given
    val apiActions = mock[SystemActions]
    val controller = new SystemController(apiActions)
    val dispatch = mock[Dispatch]
    val systemState = mock[SystemState]
    val state = mock[AdminStateDef]
    val props = mock[Props[Unit]]
    val routerProps = mock[RouterProps]
    val location = mock[LocationMock]
    val pathname = s"${SystemGroupController.path}/123/456"
    
    (routerProps.location _).expects().returning(location.asInstanceOf[Location])
    (location.pathname _).expects().returning(pathname)
    (state.systemState _).expects().returning(systemState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, props, routerProps)

    //then
    inside(result) { case SystemPanelProps(disp, actions, compState, selectedParentId, selectedId) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      compState shouldBe systemState
      selectedParentId shouldBe Some(123)
      selectedId shouldBe Some(456)
    }
  }
  
  it should "not map selected system id if path is not exact" in {
    //given
    val apiActions = mock[SystemActions]
    val controller = new SystemController(apiActions)
    val dispatch = mock[Dispatch]
    val systemState = mock[SystemState]
    val state = mock[AdminStateDef]
    val props = mock[Props[Unit]]
    val routerProps = mock[RouterProps]
    val location = mock[LocationMock]
    val pathname = s"${SystemGroupController.path}/123/456/not-exact"
    
    (routerProps.location _).expects().returning(location.asInstanceOf[Location])
    (location.pathname _).expects().returning(pathname)
    (state.systemState _).expects().returning(systemState)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, props, routerProps)

    //then
    inside(result) { case SystemPanelProps(disp, actions, compState, selectedParentId, selectedId) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      compState shouldBe systemState
      selectedParentId shouldBe Some(123)
      selectedId shouldBe None
    }
  }
}

object SystemControllerSpec {

  @JSExportAll
  trait LocationMock {

    def pathname: String
  }
}
