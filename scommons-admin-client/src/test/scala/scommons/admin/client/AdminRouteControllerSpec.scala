package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.client.app.{AppBrowseController, AppBrowseControllerProps}
import scommons.client.test.TestSpec
import scommons.client.ui.Buttons
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.util.BrowsePath

class AdminRouteControllerSpec extends TestSpec {

  it should "return component" in {
    //when & then
    AdminRouteController.component shouldBe AppBrowseController()
  }
  
  it should "map state to props" in {
    //given
    val props = mock[Props[Unit]]
    val expectedDispatch = mock[Dispatch]
    val expectedTreeRoots = List(BrowseTreeItemData("Test Item", BrowsePath("/test")))
    val state = mock[AdminStateDef]
    (state.treeRoots _).expects().returning(expectedTreeRoots)

    //when
    val result = AdminRouteController.mapStateToProps(expectedDispatch)(state, props)
    
    //then
    inside(result) {
      case AppBrowseControllerProps(
      buttons,
      treeRoots,
      dispatch,
      initiallyOpenedNodes
      ) =>
        buttons shouldBe List(Buttons.REFRESH, Buttons.ADD, Buttons.REMOVE, Buttons.EDIT)
        treeRoots shouldBe expectedTreeRoots
        dispatch shouldBe expectedDispatch
        initiallyOpenedNodes shouldBe Set.empty[BrowsePath]
    }
  }
}
