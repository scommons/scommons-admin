package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.client.app.BaseStateControllerRaw
import scommons.client.task.{TaskManager, TaskManagerProps}

object AdminTaskController
  extends BaseStateControllerRaw[AdminStateDef, TaskManagerProps] {

  lazy val component: ReactClass = TaskManager()

  def mapStateToProps(dispatch: Dispatch)
                     (state: AdminStateDef, props: Props[Unit]): TaskManagerProps = {

    TaskManagerProps(state.currentTask)
  }
}