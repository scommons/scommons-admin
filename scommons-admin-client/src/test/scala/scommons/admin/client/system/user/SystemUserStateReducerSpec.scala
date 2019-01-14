package scommons.admin.client.system.user

import org.joda.time.DateTime
import scommons.admin.client.api.role.permission.RolePermissionData
import scommons.admin.client.api.system.user._
import scommons.admin.client.system.user.SystemUserActions._
import scommons.client.task.FutureTask
import scommons.react.test.TestSpec

import scala.concurrent.Future

class SystemUserStateReducerSpec extends TestSpec {

  private val reduce = SystemUserStateReducer.apply _
  
  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe SystemUserState()
  }
  
  it should "set params when SystemUserParamsChangedAction" in {
    //given
    val params = SystemUserParams(Some(1), Some(2))

    //when & then
    reduce(Some(SystemUserState()), SystemUserParamsChangedAction(params)) shouldBe {
      SystemUserState(params = params)
    }
  }
  
  it should "set offset when SystemUserListFetchAction" in {
    //given
    val task = FutureTask("test task", Future.successful(SystemUserListResp(Nil, None)))
    val offset = Some(123)
    
    //when & then
    reduce(Some(SystemUserState()), SystemUserListFetchAction(task, offset)) shouldBe SystemUserState(
      offset = offset
    )
  }
  
  it should "set dataList and totalCount when SystemUserListFetchedAction" in {
    //given
    val data = mock[SystemUserData]
    val dataList = List(data)
    val totalCount = Some(123)
    
    //when & then
    reduce(Some(SystemUserState()), SystemUserListFetchedAction(dataList, totalCount)) shouldBe SystemUserState(
      dataList = dataList,
      totalCount = totalCount
    )

    //when & then
    reduce(Some(SystemUserState(
      totalCount = totalCount
    )), SystemUserListFetchedAction(dataList, None)) shouldBe SystemUserState(
      dataList = dataList,
      totalCount = totalCount
    )
  }

  it should "reset data in state when SystemUserRoleFetchedAction(None)" in {
    //given
    val data = SystemUserRoleRespData(
      roles = List(
        SystemUserRoleData(1, "test role 1", isSelected = false),
        SystemUserRoleData(2, "test role 2", isSelected = true)
      ),
      permissions = List(
        RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false),
        RolePermissionData(2, Some(1), isNode = false, "test permission 1", isEnabled = false),
        RolePermissionData(3, Some(1), isNode = false, "test permission 2", isEnabled = true)
      ),
      systemUser = SystemUserData(
        userId = 1,
        login = "test_login_1",
        lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
        updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
        createdAt = DateTime("2018-12-03T11:29:01.234Z"),
        version = 123
      )
    )

    //when & then
    reduce(Some(SystemUserState(
      selectedUser = Some(data.systemUser),
      userRoles = data.roles,
      permissionsByParentId = data.permissions.groupBy(_.parentId)
    )), SystemUserRoleFetchedAction(None)) shouldBe {
      SystemUserState(
        selectedUser = None,
        userRoles = Nil,
        permissionsByParentId = Map.empty
      )
    }
  }

  it should "set data in state when SystemUserRoleFetchedAction(Some)" in {
    //given
    val data = SystemUserRoleRespData(
      roles = List(
        SystemUserRoleData(1, "test role 1", isSelected = false),
        SystemUserRoleData(2, "test role 2", isSelected = true)
      ),
      permissions = List(
        RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false),
        RolePermissionData(2, Some(1), isNode = false, "test permission 1", isEnabled = false),
        RolePermissionData(3, Some(1), isNode = false, "test permission 2", isEnabled = true)
      ),
      systemUser = SystemUserData(
        userId = 1,
        login = "test_login_1",
        lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
        updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
        createdAt = DateTime("2018-12-03T11:29:01.234Z"),
        version = 123
      )
    )

    //when & then
    reduce(Some(SystemUserState()), SystemUserRoleFetchedAction(Some(data))) shouldBe {
      SystemUserState(
        selectedUser = Some(data.systemUser),
        userRoles = data.roles,
        permissionsByParentId = data.permissions.groupBy(_.parentId)
      )
    }
  }

  it should "set data in state when SystemUserRoleAddedAction" in {
    //given
    val data = SystemUserRoleRespData(
      roles = List(
        SystemUserRoleData(1, "test role 1", isSelected = false),
        SystemUserRoleData(2, "test role 2", isSelected = true)
      ),
      permissions = List(
        RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false),
        RolePermissionData(2, Some(1), isNode = false, "test permission 1", isEnabled = false),
        RolePermissionData(3, Some(1), isNode = false, "test permission 2", isEnabled = true)
      ),
      systemUser = SystemUserData(
        userId = 1,
        login = "test_login_1",
        lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
        updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
        createdAt = DateTime("2018-12-03T11:29:01.234Z"),
        version = 123
      )
    )

    //when & then
    reduce(Some(SystemUserState()), SystemUserRoleAddedAction(data)) shouldBe {
      SystemUserState(
        selectedUser = Some(data.systemUser),
        userRoles = data.roles,
        permissionsByParentId = data.permissions.groupBy(_.parentId)
      )
    }
  }

  it should "set data in state when SystemUserRoleRemovedAction" in {
    //given
    val data = SystemUserRoleRespData(
      roles = List(
        SystemUserRoleData(1, "test role 1", isSelected = false),
        SystemUserRoleData(2, "test role 2", isSelected = true)
      ),
      permissions = List(
        RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false),
        RolePermissionData(2, Some(1), isNode = false, "test permission 1", isEnabled = false),
        RolePermissionData(3, Some(1), isNode = false, "test permission 2", isEnabled = true)
      ),
      systemUser = SystemUserData(
        userId = 1,
        login = "test_login_1",
        lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
        updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
        createdAt = DateTime("2018-12-03T11:29:01.234Z"),
        version = 123
      )
    )

    //when & then
    reduce(Some(SystemUserState()), SystemUserRoleRemovedAction(data)) shouldBe {
      SystemUserState(
        selectedUser = Some(data.systemUser),
        userRoles = data.roles,
        permissionsByParentId = data.permissions.groupBy(_.parentId)
      )
    }
  }
}
