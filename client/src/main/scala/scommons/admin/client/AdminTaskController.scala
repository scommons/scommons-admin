package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.client.controller.BaseStateController
import scommons.client.task.{TaskManager, TaskManagerProps}
import scommons.react.UiComponent

object AdminTaskController
  extends BaseStateController[AdminStateDef, TaskManagerProps] {

  lazy val uiComponent: UiComponent[TaskManagerProps] = TaskManager

  def mapStateToProps(dispatch: Dispatch, state: AdminStateDef, props: Props[Unit]): TaskManagerProps = {
    TaskManagerProps(state.currentTask)
  }
}
