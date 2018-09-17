package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.user.UserActions._
import scommons.admin.client.{AdminImagesCss, AdminStateDef}
import scommons.client.test.TestSpec
import scommons.client.ui.Buttons
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.util.BrowsePath

class UserControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[UserActions]
    val controller = new UserController(apiActions)
    
    //when & then
    controller.uiComponent shouldBe UserPanel
  }
  
  it should "map state to props" in {
    //given
    val apiActions = mock[UserActions]
    val props = mock[Props[Unit]]
    val controller = new UserController(apiActions)
    val dispatch = mock[Dispatch]
    val userState = mock[UserState]
    val state = mock[AdminStateDef]
    (state.userState _).expects().returning(userState)

    //when
    val result = controller.mapStateToProps(dispatch, state, props)
    
    //then
    inside(result) { case UserPanelProps(disp, actions, resultState) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      resultState shouldBe userState
    }
  }

  it should "setup users item" in {
    //given
    val apiActions = mock[UserActions]
    val controller = new UserController(apiActions)
    val userListFetchAction = mock[UserListFetchAction]
    val expectedActions = Map(
      Buttons.REFRESH.command -> userListFetchAction
    )
    val usersPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    (apiActions.userListFetch _).expects(dispatch, None, None)
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
