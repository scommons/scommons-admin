package scommons.admin.client

import scommons.admin.client.company.{CompanyState, CompanyStateReducer}
import scommons.admin.client.system.group.{SystemGroupState, SystemGroupStateReducer}
import scommons.client.task.AbstractTask.AbstractTaskKey
import scommons.client.task.TaskAction

trait AdminStateDef {

  def currentTask: Option[AbstractTaskKey]
  def companyState: CompanyState
  def systemGroupState: SystemGroupState
}

case class AdminState(currentTask: Option[AbstractTaskKey],
                      companyState: CompanyState,
                      systemGroupState: SystemGroupState) extends AdminStateDef

object AdminStateReducer {

  def reduce(state: Option[AdminState], action: Any): AdminState = AdminState(
    currentTask = currentTaskReducer(state.flatMap(_.currentTask), action),
    companyState = CompanyStateReducer(state.map(_.companyState), action),
    systemGroupState = SystemGroupStateReducer(state.map(_.systemGroupState), action)
  )

  private def currentTaskReducer(currentTask: Option[AbstractTaskKey],
                                 action: Any): Option[AbstractTaskKey] = action match {

    case a: TaskAction => Some(a.task.key)
    case _ => None
  }
}
