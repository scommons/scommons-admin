package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps
import scommons.admin.client.AdminRouteController.buildUsersPath
import scommons.admin.client.api.user.UserListResp
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions._
import scommons.admin.client.user.system.{UserSystemActions, UserSystemState}
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

class UserControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class Actions {
    val userListFetch = mockFunction[Dispatch, Option[Int], Option[String], UserListFetchAction]

    val actions = new MockUserActions(
      userListFetchMock = userListFetch
    )
  }

  //noinspection TypeAnnotation
  class State {
    val userState = mockFunction[UserState]
    val userSystemState = mockFunction[UserSystemState]

    val state = new MockAdminStateDef(
      userStateMock = userState,
      userSystemStateMock = userSystemState
    )
  }

  it should "return component" in {
    //given
    val companyActions = mock[CompanyActions]
    val userActions = new MockUserActions
    val userSystemActions = mock[UserSystemActions]
    val controller = new UserController(companyActions, userActions, userSystemActions)
    
    //when & then
    controller.uiComponent shouldBe UserPanel
  }
  
  it should "map state to props" in {
    //given
    val companyActions = mock[CompanyActions]
    val userActions = new MockUserActions
    val userSystemActions = mock[UserSystemActions]
    val controller = new UserController(companyActions, userActions, userSystemActions)
    val dispatch = mockFunction[Any, Any]
    val userState = mock[UserState]
    val userSystemState = mock[UserSystemState]
    val state = new State
    val pushMock = mockFunction[String, Unit]
    val routeParams = new RouteParams(new RouterProps(Props[Unit](literal(
      "location" -> literal("pathname" -> "/users/123/profile"),
      "history" -> literal("push" -> pushMock)
    ))))
    val params = UserParams(Some(456), Some(UserDetailsTab.apps))
    val path = buildUsersPath(params)

    state.userState.expects().returning(userState)
    state.userSystemState.expects().returning(userSystemState)
    pushMock.expects(path.value)
    dispatch.expects(UserParamsChangedAction(params))

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, routeParams)
    
    //then
    inside(result) {
      case UserPanelProps(
      disp,
      resCompActions,
      resUserActions,
      resUserSystemActions,
      resState,
      resSystemState,
      selectedParams,
      onChangeParams
      ) =>
        disp shouldBe dispatch
        resCompActions shouldBe companyActions
        resUserActions shouldBe userActions
        resUserSystemActions shouldBe userSystemActions
        resState shouldBe userState
        resSystemState shouldBe userSystemState
        selectedParams shouldBe UserParams(Some(123), Some(UserDetailsTab.profile))
        onChangeParams(params)
    }
  }

  it should "setup users item" in {
    //given
    val companyActions = mock[CompanyActions]
    val userActions = new Actions
    val userSystemActions = mock[UserSystemActions]
    val controller = new UserController(companyActions, userActions.actions, userSystemActions)
    val userListFetchAction =
      UserListFetchAction(FutureTask("Fetching", Future.successful(UserListResp(Nil, None))), None)
    val expectedActions = Map(
      Buttons.REFRESH.command -> userListFetchAction
    )
    val usersPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    userActions.userListFetch.expects(dispatch, None, None)
      .returning(userListFetchAction)
    dispatch.expects(userListFetchAction)
      .returning(*)

    //when
    val result = controller.getUsersItem(usersPath)

    //then
    inside(result) {
      case BrowseTreeItemData(
      text,
      path,
      image,
      actions,
      reactClass
      ) =>
        text shouldBe "Users"
        path shouldBe usersPath
        image shouldBe Some(AdminImagesCss.group)
        reactClass should not be None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
}
