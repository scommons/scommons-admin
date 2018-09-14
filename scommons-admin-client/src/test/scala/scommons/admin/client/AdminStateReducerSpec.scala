package scommons.admin.client

import scommons.admin.client.api.company.CompanyListResp
import scommons.admin.client.company.CompanyActions._
import scommons.admin.client.company.CompanyState
import scommons.admin.client.role.RoleState
import scommons.admin.client.role.permission.RolePermissionState
import scommons.admin.client.system.SystemState
import scommons.admin.client.system.group.SystemGroupState
import scommons.admin.client.user.UserState
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
      userState,
      systemGroupState,
      systemState,
      roleState,
      rolePermissionState
      ) =>
        currentTask shouldBe None
        companyState shouldBe CompanyState()
        userState shouldBe UserState()
        systemGroupState shouldBe SystemGroupState()
        systemState shouldBe SystemState()
        roleState shouldBe RoleState()
        rolePermissionState shouldBe RolePermissionState()
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
