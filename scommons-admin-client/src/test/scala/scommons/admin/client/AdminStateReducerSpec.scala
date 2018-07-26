package scommons.admin.client

import scommons.admin.client.api.company.CompanyListResp
import scommons.admin.client.company.CompanyState
import scommons.admin.client.company.action._
import scommons.admin.client.system.group.SystemGroupState
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec

import scala.concurrent.Future

class AdminStateReducerSpec extends TestSpec {

  it should "return initial state" in {
    //when
    val result = AdminStateReducer.reduce(None, "")
    
    //then
    inside(result) {
      case AdminState(
      currentTask,
      companyState,
      systemGroupState
      ) =>
        currentTask shouldBe None
        companyState shouldBe CompanyState()
        systemGroupState shouldBe SystemGroupState()
    }
  }
  
  it should "set currentTask when TaskAction" in {
    //given
    val task = FutureTask("test task", Future.successful(CompanyListResp(Nil)))
    
    //when & then
    val result = AdminStateReducer.reduce(None, CompanyListFetchAction(task, None))
    result.currentTask shouldBe Some(task.key)
    
    //when & then
    AdminStateReducer.reduce(Some(result), CompanyCreateRequestAction(true))
      .currentTask shouldBe None
  }
}
