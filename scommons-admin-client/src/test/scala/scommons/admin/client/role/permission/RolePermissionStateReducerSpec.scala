package scommons.admin.client.role.permission

import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.role.permission._
import scommons.admin.client.role.permission.RolePermissionActions._
import scommons.client.test.TestSpec

class RolePermissionStateReducerSpec extends TestSpec {

  private val reduce = RolePermissionStateReducer.apply _

  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe RolePermissionState()
  }
  
  it should "set data in state when RolePermissionFetchedAction" in {
    //given
    val data = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false),
      RolePermissionData(2, Some(1), isNode = false, "test permission 1", isEnabled = false),
      RolePermissionData(3, Some(1), isNode = false, "test permission 2", isEnabled = true)
    ),
      RoleData(Some(11), 22, "test title")
    )
    
    //when & then
    reduce(Some(RolePermissionState()), RolePermissionFetchedAction(data)) shouldBe {
      RolePermissionState(
        permissionsByParentId = data.permissions.groupBy(_.parentId),
        role = Some(data.role)
      )
    }
  }
  
  it should "set data in state when RolePermissionAddedAction" in {
    //given
    val data = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false),
      RolePermissionData(2, Some(1), isNode = false, "test permission 1", isEnabled = false),
      RolePermissionData(3, Some(1), isNode = false, "test permission 2", isEnabled = true)
    ),
      RoleData(Some(11), 22, "test title")
    )
    
    //when & then
    reduce(Some(RolePermissionState()), RolePermissionAddedAction(data)) shouldBe {
      RolePermissionState(
        permissionsByParentId = data.permissions.groupBy(_.parentId),
        role = Some(data.role)
      )
    }
  }
  
  it should "set data in state when RolePermissionRemovedAction" in {
    //given
    val data = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false),
      RolePermissionData(2, Some(1), isNode = false, "test permission 1", isEnabled = false),
      RolePermissionData(3, Some(1), isNode = false, "test permission 2", isEnabled = true)
    ),
      RoleData(Some(11), 22, "test title")
    )
    
    //when & then
    reduce(Some(RolePermissionState()), RolePermissionRemovedAction(data)) shouldBe {
      RolePermissionState(
        permissionsByParentId = data.permissions.groupBy(_.parentId),
        role = Some(data.role)
      )
    }
  }
}
