package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.system.group._
import scommons.admin.client.system.group.action._
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.popup._

import scala.concurrent.Future

class SystemGroupPanelSpec extends TestSpec {

  it should "dispatch SystemGroupCreateAction when onOk in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState()
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
    dispatch.expects(SystemGroupCreateRequestAction(create = false))
    dispatch.expects(action)
    
    //when
    createPopupProps.onOk(text)
  }

  it should "dispatch SystemGroupCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState()
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
    val state = SystemGroupState(List(
      SystemGroupData(Some(1), "test env 1"),
      SystemGroupData(Some(2), "test env 2")
    ))
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))
    val comp = shallowRender(<(SystemGroupPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, InputPopup)(1)
    val text = "updated env"
    val data = SystemGroupData(Some(1), text)
    val action = SystemGroupUpdateAction(
      FutureTask("Updating", Future.successful(SystemGroupResp(data)))
    )
    (actions.systemGroupUpdate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(SystemGroupUpdateRequestAction(update = false))
    dispatch.expects(action)
    
    //when
    editPopupProps.onOk(text)
  }

  it should "dispatch SystemGroupUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(List(
      SystemGroupData(Some(1), "test env 1"),
      SystemGroupData(Some(2), "test env 2")
    ))
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))
    val comp = shallowRender(<(SystemGroupPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, InputPopup)(1)

    //then
    dispatch.expects(SystemGroupUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "dispatch SystemGroupListFetchAction when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState()
    val props = SystemGroupPanelProps(dispatch, actions, state, None)
    val component = <(SystemGroupPanel())(^.wrapped := props)()
    val action = SystemGroupListFetchAction(
      FutureTask("Fetching", Future.successful(SystemGroupListResp(Nil)))
    )
    (actions.systemGroupListFetch _).expects(dispatch)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    renderIntoDocument(component)
  }

  it should "not dispatch SystemGroupListFetchAction if non empty dataList when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemGroupActions]
    val state = SystemGroupState(List(
      SystemGroupData(Some(1), "test env 1"),
      SystemGroupData(Some(2), "test env 2")
    ))
    val props = SystemGroupPanelProps(dispatch, actions, state, Some(1))
    val component = <(SystemGroupPanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    renderIntoDocument(component)
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

  private def assertSystemGroupPanel(result: ComponentInstance, props: SystemGroupPanelProps): Unit = {
    val selectedData = props.state.dataList.find(_.id == props.selectedId)

    def assertComponents(createPopup: ComponentInstance,
                         editPopup: Option[ComponentInstance]): Assertion = {

      assertComponent(createPopup, InputPopup(), { ppProps: InputPopupProps =>
        inside(ppProps) { case InputPopupProps(show, message, _, _, placeholder, initialValue) =>
          show shouldBe props.state.showCreatePopup
          message shouldBe "Enter Environment name:"
          placeholder shouldBe None
          initialValue shouldBe "New Environment"
        }
      })
      
      editPopup.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(editPopup.get, InputPopup(), { ppProps: InputPopupProps =>
          inside(ppProps) { case InputPopupProps(show, message, _, _, placeholder, initialValue) =>
            show shouldBe props.state.showEditPopup
            message shouldBe "Enter new Environment name:"
            placeholder shouldBe None
            initialValue shouldBe data.name
          }
        })
      }
      Succeeded
    }
    
    assertDOMComponent(result, <.div()(), {
      case List(createPopup) => assertComponents(createPopup, None)
      case List(createPopup, editPopup) => assertComponents(createPopup, Some(editPopup))
    })
  }
}
