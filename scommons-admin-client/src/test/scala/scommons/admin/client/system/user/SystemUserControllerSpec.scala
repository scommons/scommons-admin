package scommons.admin.client.system.user

import scommons.admin.client.system.user.SystemUserActions._
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.controller.{PathParams, RouteParams}
import scommons.client.test.TestSpec
import scommons.client.ui.Buttons
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.util.BrowsePath

class SystemUserControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val actions = mock[SystemUserActions]
    val controller = new SystemUserController(actions)
    
    //when & then
    controller.uiComponent shouldBe SystemUserPanel
  }
  
  it should "map state to props" in {
    //given
    val actions = mock[SystemUserActions]
    val controller = new SystemUserController(actions)
    val dispatch = mockFunction[Any, Any]
    val systemUserState = mock[SystemUserState]
    val state = mock[AdminStateDef]
    val routeParams = mock[RouteParams]
    val pathParams = PathParams(s"/apps/1/123/users")

    (state.systemUserState _).expects().returning(systemUserState)
    (routeParams.pathParams _).expects().returning(pathParams)

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, routeParams)
    
    //then
    inside(result) {
      case SystemUserPanelProps(
      disp,
      resActions,
      resState,
      selectedSystemId
      ) =>
        disp shouldBe dispatch
        resActions shouldBe actions
        resState shouldBe systemUserState
        selectedSystemId shouldBe Some(123)
    }
  }

  it should "setup users item" in {
    //given
    val actions = mock[SystemUserActions]
    val controller = new SystemUserController(actions)
    val systemUserListFetchAction = mock[SystemUserListFetchAction]
    val expectedActions = Map(
      Buttons.REFRESH.command -> systemUserListFetchAction
    )
    val systemId = 123
    val usersPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    (actions.systemUserListFetch _).expects(dispatch, systemId, None, None)
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
