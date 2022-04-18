package scommons.admin.client.system

import scommons.admin.client.api.system._
import scommons.admin.client.system.SystemActions._
import scommons.admin.client.system.SystemActionsSpec._
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class SystemActionsSpec extends AsyncTestSpec {

  //noinspection TypeAnnotation
  class Api {
    val getSystemById = mockFunction[Int, Future[SystemResp]]
    val listSystems = mockFunction[Future[SystemListResp]]
    val createSystem = mockFunction[SystemData, Future[SystemResp]]
    val updateSystem = mockFunction[SystemData, Future[SystemResp]]

    val api = new MockSystemApi(
      getSystemByIdMock = getSystemById,
      listSystemsMock = listSystems,
      createSystemMock = createSystem,
      updateSystemMock = updateSystem
    )
  }

  it should "dispatch SystemListFetchedAction when systemListFetch" in {
    //given
    val api = new Api
    val actions = new SystemActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val dataList = List(SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    ))
    val expectedResp = SystemListResp(dataList)

    api.listSystems.expects()
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemListFetchedAction(dataList))
    
    //when
    val SystemListFetchAction(FutureTask(message, future)) =
      actions.systemListFetch(dispatch)
    
    //then
    message shouldBe "Fetching Applications"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch SystemCreatedAction when systemCreate" in {
    //given
    val api = new Api
    val actions = new SystemActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val data = SystemData(
      id = None,
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )
    val respData = data.copy(id = Some(11))
    val expectedResp = SystemResp(respData)

    api.createSystem.expects(data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemCreatedAction(respData))
    
    //when
    val SystemCreateAction(FutureTask(message, future)) =
      actions.systemCreate(dispatch, data)
    
    //then
    message shouldBe "Creating Application"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch SystemUpdatedAction when systemUpdate" in {
    //given
    val api = new Api
    val actions = new SystemActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val data = SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )
    val respData = SystemData(
      id = Some(11),
      name = "updated name",
      password = "updated password",
      title = "updated title",
      url = "http://updated.test.com",
      parentId = 1
    )
    val expectedResp = SystemResp(respData)

    api.updateSystem.expects(data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemUpdatedAction(respData))
    
    //when
    val SystemUpdateAction(FutureTask(message, future)) =
      actions.systemUpdate(dispatch, data)
    
    //then
    message shouldBe "Updating Application"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object SystemActionsSpec {
  
  private class SystemActionsTest(api: SystemApi)
    extends SystemActions {

    protected def client: SystemApi = api
  }
}
