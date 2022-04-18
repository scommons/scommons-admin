package scommons.admin.client

import scommons.admin.client.company.CompanyState
import scommons.admin.client.role.RoleState
import scommons.admin.client.role.permission.RolePermissionState
import scommons.admin.client.system.SystemState
import scommons.admin.client.system.group.SystemGroupState
import scommons.admin.client.system.user.SystemUserState
import scommons.admin.client.user.UserState
import scommons.admin.client.user.system.UserSystemState
import scommons.react.redux.task.AbstractTask

//noinspection NotImplementedCode
class MockAdminStateDef(
  currentTaskMock: () => Option[AbstractTask] = () => ???,
  companyStateMock: () => CompanyState = () => ???,
  userStateMock: () => UserState = () => ???,
  userSystemStateMock: () => UserSystemState = () => ???,
  systemGroupStateMock: () => SystemGroupState = () => ???,
  systemStateMock: () => SystemState = () => ???,
  systemUserStateMock: () => SystemUserState = () => ???,
  roleStateMock: () => RoleState = () => ???,
  rolePermissionStateMock: () => RolePermissionState = () => ???
) extends AdminStateDef {

  override def currentTask: Option[AbstractTask] = currentTaskMock()

  override def companyState: CompanyState = companyStateMock()

  override def userState: UserState = userStateMock()

  override def userSystemState: UserSystemState = userSystemStateMock()

  override def systemGroupState: SystemGroupState = systemGroupStateMock()

  override def systemState: SystemState = systemStateMock()

  override def systemUserState: SystemUserState = systemUserStateMock()

  override def roleState: RoleState = roleStateMock()

  override def rolePermissionState: RolePermissionState = rolePermissionStateMock()
}
