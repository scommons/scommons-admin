package scommons.admin.client

import scommons.admin.client.action.ApiActions
import scommons.admin.client.api.company.CompanyListResp
import scommons.admin.client.company.CompanyState
import scommons.admin.client.company.action._
import scommons.admin.client.system.group.SystemGroupState
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec
import scommons.client.ui.{ButtonImagesCss, Buttons}

import scala.concurrent.Future

class AdminStateReducerSpec extends TestSpec {

  it should "setup companies browse tree item" in {
    //given
    val apiActions = mock[ApiActions]
    val reducer = new AdminStateReducer(apiActions)
    val companyListFetchAction = mock[CompanyListFetchAction]
    val dispatch = mockFunction[Any, Any]

    (apiActions.companyListFetch _).expects(dispatch, None, None)
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
    val apiActions = mock[ApiActions]
    val reducer = new AdminStateReducer(apiActions)
    
    //when
    val result = reducer.reduce(None, "")
    
    //then
    result.currentTask shouldBe None
    result.companyState shouldBe CompanyState()
    result.systemGroupState shouldBe SystemGroupState()
  }
  
  it should "set currentTask when TaskAction" in {
    //given
    val task = FutureTask("test task", Future.successful(CompanyListResp(Nil)))
    val apiActions = mock[ApiActions]
    val reducer = new AdminStateReducer(apiActions)
    
    //when & then
    val result = reducer.reduce(None, CompanyListFetchAction(task, None))
    result.currentTask shouldBe Some(task.key)
    
    //when & then
    reducer.reduce(Some(result), CompanyCreateRequestAction(true))
      .currentTask shouldBe None
  }
}
