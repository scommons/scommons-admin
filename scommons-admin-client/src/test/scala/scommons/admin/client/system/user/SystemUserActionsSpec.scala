package scommons.admin.client.system.user

import scommons.admin.client.api.system.user._
import scommons.admin.client.system.user.SystemUserActions._
import scommons.admin.client.system.user.SystemUserActionsSpec.SystemUserActionsTest
import scommons.client.task.FutureTask
import scommons.client.test.AsyncTestSpec

import scala.concurrent.Future

class SystemUserActionsSpec extends AsyncTestSpec {

  it should "dispatch SystemUserListFetchedAction when userListFetch" in {
    //given
    val api = mock[SystemUserApi]
    val actions = new SystemUserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val offset = Some(12)
    val symbols = Some("test")
    val dataList = List(mock[SystemUserData])
    val systemId = 123
    val totalCount = Some(12345)
    val expectedResp = SystemUserListResp(dataList, totalCount)

    (api.listSystemUsers _).expects(systemId, offset, Some(SystemUserActions.listLimit), symbols)
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemUserListFetchedAction(dataList, totalCount))
    
    //when
    val SystemUserListFetchAction(FutureTask(message, future), resultOffset) =
      actions.systemUserListFetch(dispatch, systemId, offset, symbols)
    
    //then
    resultOffset shouldBe offset
    message shouldBe "Fetching Application Users"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object SystemUserActionsSpec {
  
  private class SystemUserActionsTest(api: SystemUserApi)
    extends SystemUserActions {

    protected def client: SystemUserApi = api
  }
}
