package scommons.admin.client

import scommons.admin.client.company.{CompanyState, CompanyStateReducer}
import scommons.admin.client.role.permission.{RolePermissionState, RolePermissionStateReducer}
import scommons.admin.client.role.{RoleState, RoleStateReducer}
import scommons.admin.client.system.group.{SystemGroupState, SystemGroupStateReducer}
import scommons.admin.client.system.user.{SystemUserState, SystemUserStateReducer}
import scommons.admin.client.system.{SystemState, SystemStateReducer}
import scommons.admin.client.user.system.{UserSystemState, UserSystemStateReducer}
import scommons.admin.client.user.{UserState, UserStateReducer}
import scommons.client.task.AbstractTask.AbstractTaskKey
import scommons.client.task.TaskAction

trait AdminStateDef {

  def currentTask: Option[AbstractTaskKey]
  def companyState: CompanyState
  def userState: UserState
  def userSystemState: UserSystemState
  def systemGroupState: SystemGroupState
  def systemState: SystemState
  def systemUserState: SystemUserState
  def roleState: RoleState
  def rolePermissionState: RolePermissionState
}

case class AdminState(currentTask: Option[AbstractTaskKey],
                      companyState: CompanyState,
                      userState: UserState,
                      userSystemState: UserSystemState,
                      systemGroupState: SystemGroupState,
                      systemState: SystemState,
                      systemUserState: SystemUserState,
                      roleState: RoleState,
                      rolePermissionState: RolePermissionState) extends AdminStateDef

object AdminStateReducer {

  def reduce(state: Option[AdminState], action: Any): AdminState = AdminState(
    currentTask = currentTaskReducer(state.flatMap(_.currentTask), action),
    companyState = CompanyStateReducer(state.map(_.companyState), action),
    userState = UserStateReducer(state.map(_.userState), action),
    userSystemState = UserSystemStateReducer(state.map(_.userSystemState), action),
    systemGroupState = SystemGroupStateReducer(state.map(_.systemGroupState), action),
    systemState = SystemStateReducer(state.map(_.systemState), action),
    systemUserState = SystemUserStateReducer(state.map(_.systemUserState), action),
    roleState = RoleStateReducer(state.map(_.roleState), action),
    rolePermissionState = RolePermissionStateReducer(state.map(_.rolePermissionState), action)
  )

  private def currentTaskReducer(currentTask: Option[AbstractTaskKey],
                                 action: Any): Option[AbstractTaskKey] = action match {

    case a: TaskAction => Some(a.task.key)
    case _ => currentTask
  }
}
