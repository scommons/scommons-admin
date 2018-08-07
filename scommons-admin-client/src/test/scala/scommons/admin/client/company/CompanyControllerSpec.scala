package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminStateDef
import scommons.admin.client.company.CompanyActions._
import scommons.client.test.TestSpec
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.ui.tree.BrowseTreeItemData

class CompanyControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[CompanyActions]
    val controller = new CompanyController(apiActions)
    
    //when & then
    controller.component shouldBe CompanyPanel()
  }
  
  it should "map state to props" in {
    //given
    val apiActions = mock[CompanyActions]
    val props = mock[Props[Unit]]
    val controller = new CompanyController(apiActions)
    val dispatch = mock[Dispatch]
    val companyState = mock[CompanyState]
    val state = mock[AdminStateDef]
    (state.companyState _).expects().returning(companyState)

    //when
    val result = controller.mapStateToProps(dispatch, state, props)
    
    //then
    inside(result) { case CompanyPanelProps(disp, actions, compState) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      compState shouldBe companyState
    }
  }

  it should "setup companies item" in {
    //given
    val apiActions = mock[CompanyActions]
    val controller = new CompanyController(apiActions)
    val companyListFetchAction = mock[CompanyListFetchAction]
    val expectedActions = Map(
      Buttons.REFRESH.command -> companyListFetchAction
    )
    val dispatch = mockFunction[Any, Any]

    (apiActions.companyListFetch _).expects(dispatch, None, None)
      .returning(companyListFetchAction)
    dispatch.expects(companyListFetchAction)
      .returning(*)

    //when
    val result = controller.getCompaniesItem

    //then
    inside(result) {
      case BrowseTreeItemData(
      text,
      path,
      image,
      actions,
      reactClass
      ) =>
        text shouldBe "Companies"
        path.value shouldBe "/companies"
        image shouldBe Some(ButtonImagesCss.folder)
        reactClass should not be None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
}