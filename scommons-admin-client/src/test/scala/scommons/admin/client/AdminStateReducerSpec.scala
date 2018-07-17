package scommons.admin.client

import scommons.admin.client.api.company.CompanyListResp
import scommons.admin.client.company.CompanyState
import scommons.admin.client.company.action._
import scommons.api.ApiStatus
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec
import scommons.client.ui.{ButtonImagesCss, Buttons}

import scala.concurrent.Future

class AdminStateReducerSpec extends TestSpec {

  it should "setup companies browse tree item" in {
    //given
    val companyActions = mock[CompanyActions]
    val reducer = new AdminStateReducer(companyActions)
    val companyListFetchAction = mock[CompanyListFetchAction]
    val dispatch = mockFunction[Any, Any]

    (companyActions.companyListFetch _).expects(dispatch, None, None)
      .returning(companyListFetchAction)
    dispatch.expects(companyListFetchAction)
      .returning(*)
    
    //when
    val result = reducer.companiesItem
    
    //then
    result.text shouldBe "Companies"
    result.path.value shouldBe "/companies"
    result.image shouldBe Some(ButtonImagesCss.folder)
    result.reactClass should not be None
    result.actions.enabledCommands shouldBe Set(Buttons.REFRESH.command)
    result.actions.onCommand(dispatch)(Buttons.REFRESH.command) shouldBe companyListFetchAction
  }
  
  it should "return initial state" in {
    //given
    val companyActions = mock[CompanyActions]
    val reducer = new AdminStateReducer(companyActions)
    
    //when
    val result = reducer.reduce(None, "")
    
    //then
    result.currentTask shouldBe None
    result.treeRoots shouldBe List(
      reducer.companiesItem
    )
    result.companyState shouldBe CompanyState()
  }
  
  it should "set currentTask when TaskAction" in {
    //given
    val task = FutureTask("test task", Future.successful(CompanyListResp(ApiStatus.Ok)))
    val companyActions = mock[CompanyActions]
    val reducer = new AdminStateReducer(companyActions)
    
    //when & then
    val result = reducer.reduce(None, CompanyListFetchAction(task, None))
    result.currentTask shouldBe Some(task.key)
    
    //when & then
    reducer.reduce(Some(result), CompanyCreateRequestAction(true))
      .currentTask shouldBe None
  }
}
