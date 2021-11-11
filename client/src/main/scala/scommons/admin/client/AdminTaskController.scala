package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import scommons.client.app.AppTaskManagerUi
import scommons.react.UiComponent
import scommons.react.redux._
import scommons.react.redux.task.{TaskManager, TaskManagerProps}

object AdminTaskController
  extends BaseStateController[AdminStateDef, TaskManagerProps] {

  lazy val uiComponent: UiComponent[TaskManagerProps] = {
    TaskManager.uiComponent = AppTaskManagerUi
    TaskManager.errorHandler = AppTaskManagerUi.errorHandler
    TaskManager
  }

  def mapStateToProps(dispatch: Dispatch, state: AdminStateDef, props: Props[Unit]): TaskManagerProps = {
    TaskManagerProps(state.currentTask)
  }
}
