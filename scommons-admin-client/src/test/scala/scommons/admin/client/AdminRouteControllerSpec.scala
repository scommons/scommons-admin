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
    //given
    val reducer = mock[AdminStateReducer]
    val controller = new AdminRouteController(reducer)

    //when & then
    controller.component shouldBe AppBrowseController()
  }
  
  it should "map state to props" in {
    //given
    val reducer = mock[AdminStateReducer]
    val controller = new AdminRouteController(reducer)
    val props = mock[Props[Unit]]
    val expectedDispatch = mock[Dispatch]
    val expectedTreeRoots = List(BrowseTreeItemData("Test Item", BrowsePath("/test")))
    val expectedOpenedNodes = Set(BrowsePath("/test_opened"))
    val state = mock[AdminStateDef]
    (reducer.getTreeRoots _).expects(state).returning(expectedTreeRoots)
    (reducer.getInitiallyOpenedNodes _).expects().returning(expectedOpenedNodes)

    //when
    val result = controller.mapStateToProps(expectedDispatch)(state, props)
    
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
        initiallyOpenedNodes shouldBe expectedOpenedNodes
    }
  }
}
