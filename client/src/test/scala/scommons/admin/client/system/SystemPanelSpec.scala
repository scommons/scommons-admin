package scommons.admin.client.system

import org.scalatest._
import scommons.admin.client.api.system._
import scommons.admin.client.system.SystemActions._
import scommons.admin.client.system.SystemPanel._
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test._

import scala.concurrent.Future

class SystemPanelSpec extends TestSpec with TestRendererUtils {

  SystemPanel.systemEditPopup = mockUiComponent("SystemEditPopup")
  SystemPanel.systemEditPanel = mockUiComponent("SystemEditPanel")

  //noinspection TypeAnnotation
  class Actions {
    val systemListFetch = mockFunction[Dispatch, SystemListFetchAction]
    val systemCreate = mockFunction[Dispatch, SystemData, SystemCreateAction]
    val systemUpdate = mockFunction[Dispatch, SystemData, SystemUpdateAction]

    val actions = new MockSystemActions(
      systemListFetchMock = systemListFetch,
      systemCreateMock = systemCreate,
      systemUpdateMock = systemUpdate
    )
  }

  it should "dispatch SystemCreateAction when onSave in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = SystemState(List(SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )).groupBy(_.parentId))
    val props = {
      val props = SystemPanelProps(dispatch, actions.actions, state, Some(1), None)
      props.copy(state = props.state.copy(showCreatePopup = true))
    }
    val comp = createTestRenderer(<(SystemPanel())(^.wrapped := props)()).root
    val createPopupProps = findComponentProps(comp, systemEditPopup)
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
    actions.systemCreate.expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    createPopupProps.onSave(data)
  }

  it should "dispatch SystemCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new MockSystemActions
    val state = SystemState(List(SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )).groupBy(_.parentId))
    val props = {
      val props = SystemPanelProps(dispatch, actions, state, Some(1), None)
      props.copy(state = props.state.copy(showCreatePopup = true))
    }
    val comp = createTestRenderer(<(SystemPanel())(^.wrapped := props)()).root
    val createPopupProps = findComponentProps(comp, systemEditPopup)

    //then
    dispatch.expects(SystemCreateRequestAction(create = false))
    
    //when
    createPopupProps.onCancel()
  }

  it should "dispatch SystemUpdateAction when onSave in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val existingData = SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )
    val state = SystemState(List(existingData).groupBy(_.parentId))
    val props = SystemPanelProps(dispatch, actions.actions, state.copy(showEditPopup = true), Some(1), Some(11))
    val comp = createTestRenderer(<(SystemPanel())(^.wrapped := props)()).root
    val editPopupProps = findComponentProps(comp, systemEditPopup)
    val data = existingData.copy(name = "updated name")
    val action = SystemUpdateAction(
      FutureTask("Updating", Future.successful(SystemResp(data)))
    )
    actions.systemUpdate.expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    editPopupProps.onSave(data)
  }

  it should "dispatch SystemUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new MockSystemActions
    val state = SystemState(List(SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    )).groupBy(_.parentId))
    val props = SystemPanelProps(dispatch, actions, state.copy(showEditPopup = true), Some(1), Some(11))
    val comp = createTestRenderer(<(SystemPanel())(^.wrapped := props)()).root
    val editPopupProps = findComponentProps(comp, systemEditPopup)

    //then
    dispatch.expects(SystemUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "dispatch SystemListFetchAction if empty systems when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = SystemState()
    val props = SystemPanelProps(dispatch, actions.actions, state, None, None)
    val action = SystemListFetchAction(
      FutureTask("Fetching", Future.successful(SystemListResp(Nil)))
    )
    actions.systemListFetch.expects(dispatch)
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
    val actions = new MockSystemActions
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
    val actions = new MockSystemActions
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
    val result = createTestRenderer(component).root
    
    //then
    assertSystemPanel(result, props)
  }

  it should "render component and show create popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = new MockSystemActions
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
    val result = createTestRenderer(component).root
    
    //then
    assertSystemPanel(result, props)
  }

  it should "render component and show edit popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = new MockSystemActions
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
    val result = createTestRenderer(component).root
    
    //then
    assertSystemPanel(result, props)
  }

  private def assertSystemPanel(result: TestInstance, props: SystemPanelProps): Unit = {
    val selectedData = props.selectedParentId.flatMap { parentId =>
      props.state.systemsByParentId.getOrElse(parentId, Nil)
        .find(_.id == props.selectedId)
    }

    def assertComponents(createPopup: Option[TestInstance],
                         editPanel: Option[TestInstance],
                         editPopup: Option[TestInstance]): Assertion = {
      
      createPopup.isDefined shouldBe (props.selectedParentId.isDefined && props.state.showCreatePopup)
      props.selectedParentId.foreach { parentId =>
        if (props.state.showCreatePopup) {
          assertTestComponent(createPopup.get, systemEditPopup) {
            case SystemEditPopupProps(title, initialData, _, _) =>
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
      }
      
      editPopup.isDefined shouldBe (selectedData.isDefined && props.state.showEditPopup)
      selectedData.foreach { data =>
        assertTestComponent(editPanel.get, systemEditPanel) {
          case SystemEditPanelProps(readOnly, initialData, requestFocus, _, _) =>
            readOnly shouldBe true
            initialData shouldBe data
            requestFocus shouldBe false
        }
        
        if (props.state.showEditPopup) {
          assertTestComponent(editPopup.get, systemEditPopup) {
            case SystemEditPopupProps(title, initialData, _, _) =>
              title shouldBe "Edit Application"
              initialData shouldBe data
          }
        }
      }
      Succeeded
    }
    
    inside(result.children.toList) {
      case List() => assertComponents(None, None, None)
      case List(createPopup) if props.state.showCreatePopup =>
        assertComponents(Some(createPopup), None, None)
      case List(editPanel, editPopup) if props.state.showEditPopup =>
        assertComponents(None, Some(editPanel), Some(editPopup))
      case List(editPanel) =>
        assertComponents(None, Some(editPanel), None)
    }
  }
}
