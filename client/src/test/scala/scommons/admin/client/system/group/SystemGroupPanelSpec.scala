package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.system.group._
import scommons.admin.client.system.group.SystemGroupActions._
import scommons.client.ui.popup._
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.{ShallowRendererUtils, TestRendererUtils}

import scala.concurrent.Future

class SystemGroupPanelSpec extends TestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "dispatch SystemGroupCreateAction when onOk in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(showCreatePopup = true)
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))
    val comp = shallowRender(<(SystemGroupPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, InputPopup)
    val text = "new env"
    val action = SystemGroupCreateAction(
      FutureTask("Creating", Future.successful(SystemGroupResp(SystemGroupData(Some(1), text))))
    )
    (actions.systemGroupCreate _).expects(dispatch, text)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    createPopupProps.onOk(text)
  }

  it should "dispatch SystemGroupCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(showCreatePopup = true)
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))
    val comp = shallowRender(<(SystemGroupPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, InputPopup)

    //then
    dispatch.expects(SystemGroupCreateRequestAction(create = false))
    
    //when
    createPopupProps.onCancel()
  }

  it should "dispatch SystemGroupUpdateAction when onOk in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(
      dataList = List(
        SystemGroupData(Some(1), "test env 1"),
        SystemGroupData(Some(2), "test env 2")
      ),
      showEditPopup = true
    )
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))
    val comp = shallowRender(<(SystemGroupPanel())(^.wrapped := props)())
    val editPopupProps = findComponentProps(comp, InputPopup)
    val text = "updated env"
    val data = SystemGroupData(Some(1), text)
    val action = SystemGroupUpdateAction(
      FutureTask("Updating", Future.successful(SystemGroupResp(data)))
    )
    (actions.systemGroupUpdate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    editPopupProps.onOk(text)
  }

  it should "dispatch SystemGroupUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(
      dataList = List(
        SystemGroupData(Some(1), "test env 1"),
        SystemGroupData(Some(2), "test env 2")
      ),
      showEditPopup = true
    )
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))
    val comp = shallowRender(<(SystemGroupPanel())(^.wrapped := props)())
    val editPopupProps = findComponentProps(comp, InputPopup)

    //then
    dispatch.expects(SystemGroupUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "dispatch SystemGroupListFetchAction if empty dataList when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState()
    val props = SystemGroupPanelProps(dispatch, actions, state, None)
    val action = SystemGroupListFetchAction(
      FutureTask("Fetching", Future.successful(SystemGroupListResp(Nil)))
    )
    (actions.systemGroupListFetch _).expects(dispatch)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    val renderer = createTestRenderer(<(SystemGroupPanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()
  }

  it should "not dispatch SystemGroupListFetchAction if non empty dataList when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(List(
      SystemGroupData(Some(1), "test env 1"),
      SystemGroupData(Some(2), "test env 2")
    ))
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))

    //then
    dispatch.expects(*).never()

    //when
    val renderer = createTestRenderer(<(SystemGroupPanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()
  }

  it should "render component" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(List(
      SystemGroupData(Some(1), "test env 1"),
      SystemGroupData(Some(2), "test env 2")
    ))
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))
    val component = <(SystemGroupPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemGroupPanel(result, props)
  }

  it should "render component and show create popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(
      dataList = List(
        SystemGroupData(Some(1), "test env 1"),
        SystemGroupData(Some(2), "test env 2")
      ),
      showCreatePopup = true
    )
    val props = SystemGroupPanelProps(dispatch, actions, state, None)
    val component = <(SystemGroupPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemGroupPanel(result, props)
  }

  it should "render component and show edit popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(
      dataList = List(
        SystemGroupData(Some(1), "test env 1"),
        SystemGroupData(Some(2), "test env 2")
      ),
      showEditPopup = true
    )
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))
    val component = <(SystemGroupPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemGroupPanel(result, props)
  }

  private def assertSystemGroupPanel(result: ShallowInstance, props: SystemGroupPanelProps): Unit = {
    val selectedData = props.state.dataList.find(_.id == props.selectedId)

    def assertComponents(createPopup: Option[ShallowInstance],
                         editPopup: Option[ShallowInstance]): Assertion = {

      createPopup.isDefined shouldBe props.state.showCreatePopup
      createPopup.foreach { cp =>
        assertComponent(cp, InputPopup) {
          case InputPopupProps(message, _, _, placeholder, initialValue) =>
            message shouldBe "Enter Environment name:"
            placeholder shouldBe None
            initialValue shouldBe "New Environment"
        }
      }
      
      editPopup.isDefined shouldBe (selectedData.isDefined && props.state.showEditPopup)
      selectedData.foreach { data =>
        editPopup.foreach { ep =>
          assertComponent(ep, InputPopup) {
            case InputPopupProps(message, _, _, placeholder, initialValue) =>
              message shouldBe "Enter new Environment name:"
              placeholder shouldBe None
              initialValue shouldBe data.name
          }
        }
      }
      Succeeded
    }
    
    assertNativeComponent(result, <.>()(), { children: List[ShallowInstance] =>
      children match {
        case List(createPopup) if props.state.showCreatePopup => assertComponents(Some(createPopup), None)
        case List(editPopup) if props.state.showEditPopup => assertComponents(None, Some(editPopup))
        case Nil => assertComponents(None, None)
      }
    })
  }
}
