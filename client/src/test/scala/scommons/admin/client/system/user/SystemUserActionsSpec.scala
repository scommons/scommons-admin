package scommons.admin.client.system.user

import org.joda.time.DateTime
import scommons.admin.client.api.AdminUiApiStatuses
import scommons.admin.client.api.role.permission.RolePermissionData
import scommons.admin.client.api.system.user._
import scommons.admin.client.system.user.SystemUserActions._
import scommons.admin.client.system.user.SystemUserActionsSpec.SystemUserActionsTest
import scommons.api.ApiStatus
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

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

  it should "dispatch SystemUserRoleFetchedAction(None) when systemUserRolesFetch" in {
    //given
    val api = mock[SystemUserApi]
    val actions = new SystemUserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val resp = SystemUserRoleResp(AdminUiApiStatuses.SystemUserNotFound)
    val expectedResp = SystemUserRoleResp(ApiStatus.Ok, None)
    val systemId = 22
    val userId = 11

    (api.listSystemUserRoles _).expects(systemId, userId)
      .returning(Future.successful(resp))
    dispatch.expects(SystemUserRoleFetchedAction(None))

    //when
    val SystemUserRoleFetchAction(FutureTask(message, future)) =
      actions.systemUserRolesFetch(dispatch, systemId, userId)

    //then
    message shouldBe "Fetching User Permissions"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }

  it should "dispatch SystemUserRoleFetchedAction(Some) when systemUserRolesFetch" in {
    //given
    val api = mock[SystemUserApi]
    val actions = new SystemUserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val respData = SystemUserRoleRespData(
      roles = List(SystemUserRoleData(1, "test role 1", isSelected = true)),
      permissions = List(
        RolePermissionData(2, Some(3), isNode = false, "test permission 1", isEnabled = true)
      ),
      systemUser = SystemUserData(
        userId = 11,
        login = "test_login_1",
        lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
        updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
        createdAt = DateTime("2018-12-03T11:29:01.234Z"),
        version = 123
      )
    )
    val expectedResp = SystemUserRoleResp(respData)
    val systemId = 22

    (api.listSystemUserRoles _).expects(systemId, respData.systemUser.userId)
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemUserRoleFetchedAction(Some(respData)))

    //when
    val SystemUserRoleFetchAction(FutureTask(message, future)) =
      actions.systemUserRolesFetch(dispatch, systemId, respData.systemUser.userId)

    //then
    message shouldBe "Fetching User Permissions"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }

  it should "dispatch SystemUserRoleAddedAction when systemUserRolesAdd" in {
    //given
    val api = mock[SystemUserApi]
    val actions = new SystemUserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val respData = SystemUserRoleRespData(
      roles = List(SystemUserRoleData(1, "test role 1", isSelected = true)),
      permissions = List(
        RolePermissionData(2, Some(3), isNode = false, "test permission 1", isEnabled = true)
      ),
      systemUser = SystemUserData(
        userId = 11,
        login = "test_login_1",
        lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
        updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
        createdAt = DateTime("2018-12-03T11:29:01.234Z"),
        version = 123
      )
    )
    val expectedResp = SystemUserRoleResp(respData)
    val systemId = 22
    val data = SystemUserRoleUpdateReq(Set(1, 2, 3), 4)

    (api.addSystemUserRoles _).expects(systemId, respData.systemUser.userId, data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemUserRoleAddedAction(respData))

    //when
    val SystemUserRoleAddAction(FutureTask(message, future)) =
      actions.systemUserRolesAdd(dispatch, systemId, respData.systemUser.userId, data)

    //then
    message shouldBe "Adding User Permissions"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }

  it should "dispatch SystemUserRoleRemovedAction when systemUserRolesRemove" in {
    //given
    val api = mock[SystemUserApi]
    val actions = new SystemUserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val respData = SystemUserRoleRespData(
      roles = List(SystemUserRoleData(1, "test role 1", isSelected = true)),
      permissions = List(
        RolePermissionData(2, Some(3), isNode = false, "test permission 1", isEnabled = true)
      ),
      systemUser = SystemUserData(
        userId = 11,
        login = "test_login_1",
        lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
        updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
        createdAt = DateTime("2018-12-03T11:29:01.234Z"),
        version = 123
      )
    )
    val expectedResp = SystemUserRoleResp(respData)
    val systemId = 22
    val data = SystemUserRoleUpdateReq(Set(1, 2, 3), 4)

    (api.removeSystemUserRoles _).expects(systemId, respData.systemUser.userId, data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(SystemUserRoleRemovedAction(respData))

    //when
    val SystemUserRoleRemoveAction(FutureTask(message, future)) =
      actions.systemUserRolesRemove(dispatch, systemId, respData.systemUser.userId, data)

    //then
    message shouldBe "Removing User Permissions"
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
