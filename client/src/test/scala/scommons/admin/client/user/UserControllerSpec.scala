package scommons.admin.client.user

import scommons.admin.client.AdminRouteController.buildUsersPath
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions._
import scommons.admin.client.user.system.{UserSystemActions, UserSystemState}
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{PathParams, RouteParams}
import scommons.client.ui.Buttons
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.util.BrowsePath
import scommons.react.test.TestSpec

class UserControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val companyActions = mock[CompanyActions]
    val userActions = mock[UserActions]
    val userSystemActions = mock[UserSystemActions]
    val controller = new UserController(companyActions, userActions, userSystemActions)
    
    //when & then
    controller.uiComponent shouldBe UserPanel
  }
  
  it should "map state to props" in {
    //given
    val companyActions = mock[CompanyActions]
    val userActions = mock[UserActions]
    val userSystemActions = mock[UserSystemActions]
    val controller = new UserController(companyActions, userActions, userSystemActions)
    val dispatch = mockFunction[Any, Any]
    val userState = mock[UserState]
    val userSystemState = mock[UserSystemState]
    val state = mock[AdminStateDef]
    val routeParams = mock[RouteParams]
    val pathParams = PathParams(s"/users/123/profile")
    val params = UserParams(Some(456), Some(UserDetailsTab.apps))
    val path = buildUsersPath(params)

    (state.userState _).expects().returning(userState)
    (state.userSystemState _).expects().returning(userSystemState)
    (routeParams.pathParams _).expects().returning(pathParams)
    (routeParams.push _).expects(path.value)
    dispatch.expects(UserParamsChangedAction(params))

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, routeParams)
    
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
    val userActions = mock[UserActions]
    val userSystemActions = mock[UserSystemActions]
    val controller = new UserController(companyActions, userActions, userSystemActions)
    val userListFetchAction = mock[UserListFetchAction]
    val expectedActions = Map(
      Buttons.REFRESH.command -> userListFetchAction
    )
    val usersPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    (userActions.userListFetch _).expects(dispatch, None, None)
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
