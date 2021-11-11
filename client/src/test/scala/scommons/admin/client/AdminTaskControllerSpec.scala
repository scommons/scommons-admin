package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import scommons.react.redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskManager, TaskManagerProps}
import scommons.react.test.TestSpec

import scala.concurrent.Future

class AdminTaskControllerSpec extends TestSpec {

  it should "return component" in {
    //when & then
    AdminTaskController.uiComponent shouldBe TaskManager
  }
  
  it should "map state to props" in {
    //given
    val props = mock[Props[Unit]]
    val dispatch = mock[Dispatch]
    val currentTask = Some(FutureTask("test task", Future.successful(())))
    val state = mock[AdminStateDef]
    (state.currentTask _).expects().returning(currentTask)

    //when
    val result = AdminTaskController.mapStateToProps(dispatch, state, props)
    
    //then
    inside(result) { case TaskManagerProps(task) =>
      task shouldBe currentTask
    }
  }
}
