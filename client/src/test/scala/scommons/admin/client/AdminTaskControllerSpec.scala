package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import scommons.react.redux.Dispatch
import scommons.react.redux.task._
import scommons.react.test.TestSpec

import scala.concurrent.Future

class AdminTaskControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class State {
    val currentTask = mockFunction[Option[AbstractTask]]

    val state = new MockAdminStateDef(
      currentTaskMock = currentTask
    )
  }

  it should "return component" in {
    //when & then
    AdminTaskController.uiComponent shouldBe TaskManager
  }
  
  it should "map state to props" in {
    //given
    val props = mock[Props[Unit]]
    val dispatch = mock[Dispatch]
    val currentTask = Some(FutureTask("test task", Future.successful(())))
    val state = new State
    state.currentTask.expects().returning(currentTask)

    //when
    val result = AdminTaskController.mapStateToProps(dispatch, state.state, props)
    
    //then
    inside(result) { case TaskManagerProps(task) =>
      task shouldBe currentTask
    }
  }
}
