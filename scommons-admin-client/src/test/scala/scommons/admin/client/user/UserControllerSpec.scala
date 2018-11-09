package scommons.admin.client.user

import scommons.admin.client.AdminRouteController.buildUsersPath
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions._
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{PathParams, RouteParams}
import scommons.client.test.TestSpec
import scommons.client.ui.Buttons
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.util.BrowsePath

class UserControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val companyActions = mock[CompanyActions]
    val userActions = mock[UserActions]
    val controller = new UserController(companyActions, userActions)
    
    //when & then
    controller.uiComponent shouldBe UserPanel
  }
  
  it should "map state to props" in {
    //given
    val companyActions = mock[CompanyActions]
    val userActions = mock[UserActions]
    val controller = new UserController(companyActions, userActions)
    val dispatch = mockFunction[Any, Any]
    val userState = mock[UserState]
    val state = mock[AdminStateDef]
    val routeParams = mock[RouteParams]
    val pathParams = PathParams(s"/users/123/test")
    val path = buildUsersPath(Some(456))

    (state.userState _).expects().returning(userState)
    (routeParams.pathParams _).expects().returning(pathParams)
    (routeParams.push _).expects(path.value)
    dispatch.expects(UsersPathChangedAction(path))

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, routeParams)
    
    //then
    inside(result) {
      case UserPanelProps(disp, resCompActions, resUserActions, resultState, selectedUserId, onChangeSelect) =>
        disp shouldBe dispatch
        resCompActions shouldBe companyActions
        resUserActions shouldBe userActions
        resultState shouldBe userState
        selectedUserId shouldBe Some(123)

        onChangeSelect(Some(456))
    }
  }

  it should "setup users item" in {
    //given
    val companyActions = mock[CompanyActions]
    val userActions = mock[UserActions]
    val controller = new UserController(companyActions, userActions)
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
