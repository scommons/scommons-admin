package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.Location
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.system.SystemActions
import scommons.admin.client.system.SystemActions._
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.admin.client.system.group.SystemGroupControllerSpec.LocationMock
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.test.TestSpec
import scommons.client.ui.tree.BrowseTreeNodeData
import scommons.client.ui.{ButtonImagesCss, Buttons}

import scala.scalajs.js.annotation.JSExportAll

class SystemGroupControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val groupActions = mock[SystemGroupActions]
    val systemActions = mock[SystemActions]
    val controller = new SystemGroupController(groupActions, systemActions)

    //when & then
    controller.component shouldBe SystemGroupPanel()
  }

  it should "map state to props" in {
    //given
    val groupActions = mock[SystemGroupActions]
    val systemActions = mock[SystemActions]
    val controller = new SystemGroupController(groupActions, systemActions)
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
      actions shouldBe groupActions
      compState shouldBe systemGroupState
      selectedId shouldBe Some(123)
    }
  }

  it should "setup applications node" in {
    //given
    val groupActions = mock[SystemGroupActions]
    val systemActions = mock[SystemActions]
    val controller = new SystemGroupController(groupActions, systemActions)
    val systemGroupListFetchAction = mock[SystemGroupListFetchAction]
    val systemGroupCreateRequestAction = SystemGroupCreateRequestAction(create = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemGroupListFetchAction,
      Buttons.ADD.command -> systemGroupCreateRequestAction
    )
    val dispatch = mockFunction[Any, Any]

    (groupActions.systemGroupListFetch _).expects(dispatch)
      .returning(systemGroupListFetchAction)
    dispatch.expects(systemGroupListFetchAction).returning(*)
    dispatch.expects(systemGroupCreateRequestAction).returning(*)

    //when
    val result = controller.getApplicationsNode

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
        path.value shouldBe SystemGroupController.path
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
    val groupActions = mock[SystemGroupActions]
    val systemActions = mock[SystemActions]
    val controller = new SystemGroupController(groupActions, systemActions)
    val data = SystemGroupData(Some(1), "env 1")
    val systemListFetchAction = mock[SystemListFetchAction]
    val systemCreateRequestAction = SystemCreateRequestAction(create = true)
    val systemGroupUpdateRequestAction = SystemGroupUpdateRequestAction(update = true)
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemListFetchAction,
      Buttons.ADD.command -> systemCreateRequestAction,
      Buttons.EDIT.command -> systemGroupUpdateRequestAction
    )
    val dispatch = mockFunction[Any, Any]

    (systemActions.systemListFetch _).expects(dispatch)
      .returning(systemListFetchAction)
    dispatch.expects(systemListFetchAction).returning(*)
    dispatch.expects(systemCreateRequestAction).returning(*)
    dispatch.expects(systemGroupUpdateRequestAction).returning(*)

    //when
    val result = controller.getEnvironmentNode(data)

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
        path.value shouldBe s"${SystemGroupController.path}/${data.id.get}"
        image shouldBe Some(ButtonImagesCss.folder)
        reactClass shouldBe None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
}

object SystemGroupControllerSpec {

  @JSExportAll
  trait LocationMock {

    def pathname: String
  }
}
