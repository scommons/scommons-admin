package scommons.admin.client.company

import scommons.admin.client.api.company._
import scommons.admin.client.company.CompanyActions._
import scommons.admin.client.company.CompanyActionsSpec._
import scommons.client.task.FutureTask
import scommons.client.test.AsyncTestSpec

import scala.concurrent.Future

class CompanyActionsSpec extends AsyncTestSpec {

  it should "dispatch CompanyListFetchedAction when companyListFetch" in {
    //given
    val api = mock[CompanyApi]
    val actions = new CompanyActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val offset = Some(12)
    val symbols = Some("test")
    val dataList = List(CompanyData(Some(1), "test name"))
    val totalCount = Some(12345)
    val expectedResp = CompanyListResp(dataList, totalCount)

    (api.listCompanies _).expects(offset, Some(CompanyActions.listLimit), symbols)
      .returning(Future.successful(expectedResp))
    dispatch.expects(CompanyListFetchedAction(dataList, totalCount))
    
    //when
    val CompanyListFetchAction(FutureTask(message, future), resultOffset) =
      actions.companyListFetch(dispatch, offset, symbols)
    
    //then
    resultOffset shouldBe offset
    message shouldBe "Fetching Companies"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch CompanyCreatedAction when companyCreate" in {
    //given
    val api = mock[CompanyApi]
    val actions = new CompanyActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val name = "test name"
    val data = CompanyData(Some(1), name)
    val expectedResp = CompanyResp(data)

    (api.createCompany _).expects(CompanyData(None, name))
      .returning(Future.successful(expectedResp))
    dispatch.expects(CompanyCreatedAction(data))
    
    //when
    val CompanyCreateAction(FutureTask(message, future)) =
      actions.companyCreate(dispatch, name)
    
    //then
    message shouldBe "Creating Company"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch CompanyUpdatedAction when companyUpdate" in {
    //given
    val api = mock[CompanyApi]
    val actions = new CompanyActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val data = CompanyData(Some(1), "test name")
    val respData = CompanyData(Some(1), "updated test name")
    val expectedResp = CompanyResp(respData)

    (api.updateCompany _).expects(data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(CompanyUpdatedAction(respData))
    
    //when
    val CompanyUpdateAction(FutureTask(message, future)) =
      actions.companyUpdate(dispatch, data)
    
    //then
    message shouldBe "Updating Company"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object CompanyActionsSpec {
  
  private class CompanyActionsTest(api: CompanyApi)
    extends CompanyActions {

    protected def client: CompanyApi = api
  }
}
