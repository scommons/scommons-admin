package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React.Props
import scommons.admin.client.AdminStateDef
import scommons.admin.client.api.company.CompanyListResp
import scommons.admin.client.company.CompanyActions._
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.ui.tree.BrowseTreeItemData
import scommons.client.util.BrowsePath
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec

import scala.concurrent.Future

class CompanyControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[CompanyActions]
    val controller = new CompanyController(apiActions)
    
    //when & then
    controller.uiComponent shouldBe CompanyPanel
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
    val companyListFetchAction =
      CompanyListFetchAction(FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))), None)
    val expectedActions = Map(
      Buttons.REFRESH.command -> companyListFetchAction
    )
    val companiesPath = BrowsePath("/some-path")
    val dispatch = mockFunction[Any, Any]

    (apiActions.companyListFetch _).expects(dispatch, None, None)
      .returning(companyListFetchAction)
    dispatch.expects(companyListFetchAction)
      .returning(*)

    //when
    val result = controller.getCompaniesItem(companiesPath)

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
        path shouldBe companiesPath
        image shouldBe Some(ButtonImagesCss.folder)
        reactClass should not be None
        actions.enabledCommands shouldBe expectedActions.keySet
        expectedActions.foreach { case (cmd, action) =>
          actions.onCommand(dispatch)(cmd) shouldBe action
        }
    }
  }
}
