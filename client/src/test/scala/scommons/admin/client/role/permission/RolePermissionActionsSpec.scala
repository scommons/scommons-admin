package scommons.admin.client.role.permission

import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.role.permission._
import scommons.admin.client.role.RoleActions.RoleUpdatedAction
import scommons.admin.client.role.permission.RolePermissionActions._
import scommons.admin.client.role.permission.RolePermissionActionsSpec._
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class RolePermissionActionsSpec extends AsyncTestSpec {

  //noinspection TypeAnnotation
  class Api {
    val listRolePermissions = mockFunction[Int, Future[RolePermissionResp]]
    val addRolePermissions = mockFunction[Int, RolePermissionUpdateReq, Future[RolePermissionResp]]
    val removeRolePermissions = mockFunction[Int, RolePermissionUpdateReq, Future[RolePermissionResp]]

    val api = new MockRolePermissionApi(
      listRolePermissionsMock = listRolePermissions,
      addRolePermissionsMock = addRolePermissions,
      removeRolePermissionsMock = removeRolePermissions
    )
  }

  it should "dispatch RolePermissionFetchedAction and RoleUpdatedAction when rolePermissionsFetch" in {
    //given
    val api = new Api
    val actions = new RolePermissionActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val respData = RolePermissionRespData(
      List(RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false)),
      RoleData(Some(11), 22, "test title")
    )
    val expectedResp = RolePermissionResp(respData)

    api.listRolePermissions.expects(respData.role.id.get)
      .returning(Future.successful(expectedResp))
    dispatch.expects(RoleUpdatedAction(respData.role))
    dispatch.expects(RolePermissionFetchedAction(respData))
    
    //when
    val RolePermissionFetchAction(FutureTask(message, future)) =
      actions.rolePermissionsFetch(dispatch, respData.role.id.get)
    
    //then
    message shouldBe "Fetching Role Permissions"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch RolePermissionAddedAction and RoleUpdatedAction when rolePermissionsAdd" in {
    //given
    val api = new Api
    val actions = new RolePermissionActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val respData = RolePermissionRespData(
      List(RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false)),
      RoleData(Some(11), 22, "test title")
    )
    val data = RolePermissionUpdateReq(Set(1, 2, 3), 4)
    val expectedResp = RolePermissionResp(respData)

    api.addRolePermissions.expects(respData.role.id.get, data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(RoleUpdatedAction(respData.role))
    dispatch.expects(RolePermissionAddedAction(respData))
    
    //when
    val RolePermissionAddAction(FutureTask(message, future)) =
      actions.rolePermissionsAdd(dispatch, respData.role.id.get, data)
    
    //then
    message shouldBe "Adding Role Permissions"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch RolePermissionRemovedAction and RoleUpdatedAction when rolePermissionsRemove" in {
    //given
    val api = new Api
    val actions = new RolePermissionActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val respData = RolePermissionRespData(
      List(RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false)),
      RoleData(Some(11), 22, "test title")
    )
    val data = RolePermissionUpdateReq(Set(1, 2, 3), 4)
    val expectedResp = RolePermissionResp(respData)

    api.removeRolePermissions.expects(respData.role.id.get, data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(RoleUpdatedAction(respData.role))
    dispatch.expects(RolePermissionRemovedAction(respData))
    
    //when
    val RolePermissionRemoveAction(FutureTask(message, future)) =
      actions.rolePermissionsRemove(dispatch, respData.role.id.get, data)
    
    //then
    message shouldBe "Removing Role Permissions"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object RolePermissionActionsSpec {
  
  private class RolePermissionActionsTest(api: RolePermissionApi)
    extends RolePermissionActions {

    protected def client: RolePermissionApi = api
  }
}
