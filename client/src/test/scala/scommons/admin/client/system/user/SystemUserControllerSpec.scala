package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminRouteController.buildAppsUsersPath
import scommons.admin.client.api.system.user.SystemUserListResp
import scommons.admin.client.system.user.SystemUserActions._
import scommons.admin.client.{AdminImagesCss, MockAdminStateDef}
import scommons.client.controller.RouteParams
import scommons.client.ui.Buttons
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.util.BrowsePath
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec

import scala.concurrent.Future
import scala.scalajs.js.Dynamic.literal

class SystemUserControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class Actions {
    val systemUserListFetch = mockFunction[Dispatch, Int, Option[Int], Option[String], SystemUserListFetchAction]

    val actions = new MockSystemUserActions(
      systemUserListFetchMock = systemUserListFetch
    )
  }

  //noinspection TypeAnnotation
  class State {
    val systemUserState = mockFunction[SystemUserState]

    val state = new MockAdminStateDef(
      systemUserStateMock = systemUserState
    )
  }

  it should "return component" in {
    //given
    val actions = new Actions
    val controller = new SystemUserController(actions.actions)
    
    //when & then
    controller.uiComponent shouldBe SystemUserPanel
  }
  
  it should "map state to props" in {
    //given
    val actions = new Actions
    val controller = new SystemUserController(actions.actions)
    val dispatch = mockFunction[Any, Any]
    val systemUserState = SystemUserState()
    val state = new State
    val pushMock = mockFunction[String, Unit]
    val routeParams = new RouteParams(new RouterProps(Props[Unit](literal(
      "location" -> literal("pathname" -> "/apps/1/2/users/3"),
      "history" -> literal("push" -> pushMock)
    ))))
    val params = SystemUserParams(Some(4), Some(5), Some(6))
    val path = buildAppsUsersPath(params)

    state.systemUserState.expects().returning(systemUserState)
    
    pushMock.expects(path.value)
    dispatch.expects(SystemUserParamsChangedAction(params))

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, routeParams)
    
    //then
    inside(result) {
      case SystemUserPanelProps(
      disp,
      resActions,
      resState,
      selectedParams,
      onChangeParams
      ) =>
        disp shouldBe dispatch
        resActions shouldBe actions.actions
        resState shouldBe systemUserState
        selectedParams shouldBe SystemUserParams(Some(1), Some(2), Some(3))
        onChangeParams(params)
    }
  }

  it should "setup users item" in {
    //given
    val actions = new Actions
    val controller = new SystemUserController(actions.actions)
    val systemUserListFetchAction =
      SystemUserListFetchAction(FutureTask("Fetching", Future.successful(SystemUserListResp(Nil, None))), None)
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemUserListFetchAction
    )
    val systemId = 123
    val usersPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    actions.systemUserListFetch.expects(dispatch, systemId, None, None)
      .returning(systemUserListFetchAction)
    dispatch.expects(systemUserListFetchAction)
      .returning(*)

    //when
    val result = controller.getUsersItem(usersPath, systemId)

    //then
    inside(result) {
      case BrowseTreeItemData(
      text,
      path,
      image,
      resActions,
      reactClass
      ) =>
        text shouldBe "Users"
        path shouldBe usersPath
        image shouldBe Some(AdminImagesCss.group)
        reactClass should not be None
        resActions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          resActions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
}
