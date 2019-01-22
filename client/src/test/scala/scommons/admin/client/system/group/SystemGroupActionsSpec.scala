package scommons.admin.client.system.group

import scommons.admin.client.api.system.group._
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.admin.client.system.group.SystemGroupActionsSpec._
import scommons.client.task.FutureTask
import scommons.react.test.dom.AsyncTestSpec

import scala.concurrent.Future

class SystemGroupActionsSpec extends AsyncTestSpec {

  it should "dispatch SystemGroupListFetchedAction when systemGroupListFetch" in {
    //given
    val api = mock[SystemGroupApi]
    val actions = new SystemGroupActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val dataList = List(SystemGroupData(Some(1), "test name"))
    val expectedResp = SystemGroupListResp(dataList)

    (api.listSystemGroups _).expects()
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemGroupListFetchedAction(dataList))
    
    //when
    val SystemGroupListFetchAction(FutureTask(message, future)) =
      actions.systemGroupListFetch(dispatch)
    
    //then
    message shouldBe "Fetching Environments"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch SystemGroupCreatedAction when systemGroupCreate" in {
    //given
    val api = mock[SystemGroupApi]
    val actions = new SystemGroupActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val name = "test name"
    val data = SystemGroupData(Some(1), name)
    val expectedResp = SystemGroupResp(data)

    (api.createSystemGroup _).expects(SystemGroupData(None, name))
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemGroupCreatedAction(data))
    
    //when
    val SystemGroupCreateAction(FutureTask(message, future)) =
      actions.systemGroupCreate(dispatch, name)
    
    //then
    message shouldBe "Creating Environment"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch SystemGroupUpdatedAction when systemGroupUpdate" in {
    //given
    val api = mock[SystemGroupApi]
    val actions = new SystemGroupActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val data = SystemGroupData(Some(1), "test name")
    val respData = SystemGroupData(Some(1), "updated test name")
    val expectedResp = SystemGroupResp(respData)

    (api.updateSystemGroup _).expects(data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemGroupUpdatedAction(respData))
    
    //when
    val SystemGroupUpdateAction(FutureTask(message, future)) =
      actions.systemGroupUpdate(dispatch, data)
    
    //then
    message shouldBe "Updating Environment"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object SystemGroupActionsSpec {
  
  private class SystemGroupActionsTest(api: SystemGroupApi)
    extends SystemGroupActions {

    protected def client: SystemGroupApi = api
  }
}
