package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.system._
import scommons.admin.client.system.SystemActions._
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.{ShallowRendererUtils, TestRendererUtils}

import scala.concurrent.Future

class SystemPanelSpec extends TestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "dispatch SystemCreateAction when onSave in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemActions]
    val state = SystemState()
    val props = SystemPanelProps(dispatch, actions, state, Some(1), None)
    val comp = shallowRender(<(SystemPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, SystemEditPopup)
    val data = SystemData(
      id = None,
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )
    val action = SystemCreateAction(
      FutureTask("Creating", Future.successful(SystemResp(data.copy(id = Some(11)))))
    )
    (actions.systemCreate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    createPopupProps.onSave(data)
  }

  it should "dispatch SystemCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemActions]
    val state = SystemState()
    val props = SystemPanelProps(dispatch, actions, state, Some(1), None)
    val comp = shallowRender(<(SystemPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, SystemEditPopup)

    //then
    dispatch.expects(SystemCreateRequestAction(create = false))
    
    //when
    createPopupProps.onCancel()
  }

  it should "dispatch SystemUpdateAction when onSave in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemActions]
    val existingData = SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )
    val state = SystemState(List(existingData).groupBy(_.parentId))
    val props = SystemPanelProps(dispatch, actions, state, Some(1), Some(11))
    val comp = shallowRender(<(SystemPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, SystemEditPopup)(1)
    val data = existingData.copy(name = "updated name")
    val action = SystemUpdateAction(
      FutureTask("Updating", Future.successful(SystemResp(data)))
    )
    (actions.systemUpdate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    editPopupProps.onSave(data)
  }

  it should "dispatch SystemUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemActions]
    val state = SystemState(List(SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )).groupBy(_.parentId))
    val props = SystemPanelProps(dispatch, actions, state, Some(1), Some(11))
    val comp = shallowRender(<(SystemPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, SystemEditPopup)(1)

    //then
    dispatch.expects(SystemUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "dispatch SystemListFetchAction if empty systems when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemActions]
    val state = SystemState()
    val props = SystemPanelProps(dispatch, actions, state, None, None)
    val action = SystemListFetchAction(
      FutureTask("Fetching", Future.successful(SystemListResp(Nil)))
    )
    (actions.systemListFetch _).expects(dispatch)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    val renderer = createTestRenderer(<(SystemPanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()
  }

  it should "not dispatch SystemListFetchAction if non empty systems when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemActions]
    val state = SystemState(List(SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )).groupBy(_.parentId))
    val props = SystemPanelProps(dispatch, actions, state, None, None)

    //then
    dispatch.expects(*).never()

    //when
    val renderer = createTestRenderer(<(SystemPanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()
  }

  it should "render component" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[SystemActions]
    val state = SystemState(List(SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )).groupBy(_.parentId))
    val props = SystemPanelProps(dispatch, actions, state, Some(1), Some(11))
    val component = <(SystemPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemPanel(result, props)
  }

  it should "render component and show create popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[SystemActions]
    val state = SystemState(
      systemsByParentId = List(SystemData(
        id = Some(11),
        name = "test name",
        password = "test password",
        title = "test title",
        url = "http://test.com",
        parentId = 1
      )).groupBy(_.parentId),
      showCreatePopup = true
    )
    val props = SystemPanelProps(dispatch, actions, state, Some(1), None)
    val component = <(SystemPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemPanel(result, props)
  }

  it should "render component and show edit popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[SystemActions]
    val state = SystemState(
      systemsByParentId = List(SystemData(
        id = Some(11),
        name = "test name",
        password = "test password",
        title = "test title",
        url = "http://test.com",
        parentId = 1
      )).groupBy(_.parentId),
      showEditPopup = true
    )
    val props = SystemPanelProps(dispatch, actions, state, Some(1), Some(11))
    val component = <(SystemPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemPanel(result, props)
  }

  private def assertSystemPanel(result: ShallowInstance, props: SystemPanelProps): Unit = {
    val selectedData = props.selectedParentId.flatMap { parentId =>
      props.state.systemsByParentId.getOrElse(parentId, Nil)
        .find(_.id == props.selectedId)
    }

    def assertComponents(createPopup: Option[ShallowInstance],
                         editPanel: Option[ShallowInstance],
                         editPopup: Option[ShallowInstance]): Assertion = {
      
      createPopup.isEmpty shouldBe props.selectedParentId.isEmpty
      props.selectedParentId.foreach { parentId =>
        assertComponent(createPopup.get, SystemEditPopup) {
          case SystemEditPopupProps(show, title, initialData, _, _) =>
            show shouldBe props.state.showCreatePopup
            title shouldBe "New Application"
            initialData shouldBe SystemData(
              id = None,
              name = "",
              password = "",
              title = "",
              url = "",
              parentId = parentId
            )
        }
      }
      
      editPopup.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(editPanel.get, SystemEditPanel) {
          case SystemEditPanelProps(readOnly, initialData, requestFocus, _, _) =>
            readOnly shouldBe true
            initialData shouldBe data
            requestFocus shouldBe false
        }
        assertComponent(editPopup.get, SystemEditPopup) {
          case SystemEditPopupProps(show, title, initialData, _, _) =>
            show shouldBe props.state.showEditPopup
            title shouldBe "Edit Application"
            initialData shouldBe data
        }
      }
      Succeeded
    }
    
    assertNativeComponent(result, <.>()(), { children: List[ShallowInstance] =>
      children match {
        case List() => assertComponents(None, None, None)
        case List(createPopup) => assertComponents(Some(createPopup), None, None)
        case List(createPopup, editPanel, editPopup) =>
          assertComponents(Some(createPopup), Some(editPanel), Some(editPopup))
      }
    })
  }
}
