package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.Location
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.SystemActions._
import scommons.admin.client.system.SystemControllerSpec.LocationMock
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.test.TestSpec
import scommons.client.ui.Buttons
import scommons.client.ui.tree.BrowseTreeNodeData

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
    val pathname = "/apps/123/456"
    
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
    val pathname = "/apps/123/456/not-exact"
    
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

  it should "setup application node" in {
    //given
    val apiActions = mock[SystemActions]
    val controller = new SystemController(apiActions)
    val parentId = 1
    val parentPath = s"/apps/$parentId"
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

object SystemControllerSpec {

  @JSExportAll
  trait LocationMock {

    def pathname: String
  }
}
