package scommons.admin.client.role

import scommons.admin.client.api.role._
import scommons.admin.client.role.RoleActions._
import scommons.admin.client.role.RoleActionsSpec._
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class RoleActionsSpec extends AsyncTestSpec {

  //noinspection TypeAnnotation
  class Api {
    val getRoleById = mockFunction[Int, Future[RoleResp]]
    val listRoles = mockFunction[Future[RoleListResp]]
    val createRole = mockFunction[RoleData, Future[RoleResp]]
    val updateRole = mockFunction[RoleData, Future[RoleResp]]

    val api = new MockRoleApi(
      getRoleByIdMock = getRoleById,
      listRolesMock = listRoles,
      createRoleMock = createRole,
      updateRoleMock = updateRole
    )
  }

  it should "dispatch RoleListFetchedAction when roleListFetch" in {
    //given
    val api = new Api
    val actions = new RoleActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val dataList = List(RoleData(
      id = Some(11),
      systemId = 1,
      title = "test title"
    ))
    val expectedResp = RoleListResp(dataList)

    api.listRoles.expects()
      .returning(Future.successful(expectedResp))
    dispatch.expects(RoleListFetchedAction(dataList))
    
    //when
    val RoleListFetchAction(FutureTask(message, future)) =
      actions.roleListFetch(dispatch)
    
    //then
    message shouldBe "Fetching Roles"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch RoleCreatedAction when roleCreate" in {
    //given
    val api = new Api
    val actions = new RoleActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val data = RoleData(
      id = None,
      systemId = 1,
      title = "test title"
    )
    val respData = data.copy(id = Some(11))
    val expectedResp = RoleResp(respData)

    api.createRole.expects(data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(RoleCreatedAction(respData))
    
    //when
    val RoleCreateAction(FutureTask(message, future)) =
      actions.roleCreate(dispatch, data)
    
    //then
    message shouldBe "Creating Role"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch RoleUpdatedAction when roleUpdate" in {
    //given
    val api = new Api
    val actions = new RoleActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val data = RoleData(
      id = Some(11),
      systemId = 1,
      title = "test title"
    )
    val respData = RoleData(
      id = Some(11),
      systemId = 1,
      title = "updated title"
    )
    val expectedResp = RoleResp(respData)

    api.updateRole.expects(data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(RoleUpdatedAction(respData))
    
    //when
    val RoleUpdateAction(FutureTask(message, future)) =
      actions.roleUpdate(dispatch, data)
    
    //then
    message shouldBe "Updating Role"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object RoleActionsSpec {
  
  private class RoleActionsTest(api: RoleApi)
    extends RoleActions {

    protected def client: RoleApi = api
  }
}
