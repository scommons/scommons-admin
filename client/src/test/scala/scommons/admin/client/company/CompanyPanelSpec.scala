package scommons.admin.client.company

import org.scalatest._
import scommons.admin.client.api.company._
import scommons.admin.client.company.CompanyActions._
import scommons.admin.client.company.CompanyPanel._
import scommons.client.ui._
import scommons.client.ui.popup._
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test._

import scala.concurrent.Future

class CompanyPanelSpec extends TestSpec with TestRendererUtils {

  CompanyPanel.buttonsPanel = mockUiComponent("ButtonsPanel")
  CompanyPanel.companyTablePanel = mockUiComponent("CompanyTablePanel")
  CompanyPanel.inputPopup = mockUiComponent("InputPopup")

  //noinspection TypeAnnotation
  class Actions {
    val companyListFetch = mockFunction[Dispatch, Option[Int], Option[String], CompanyListFetchAction]
    val companyCreate = mockFunction[Dispatch, String, CompanyCreateAction]
    val companyUpdate = mockFunction[Dispatch, CompanyData, CompanyUpdateAction]

    val actions = new MockCompanyActions(
      companyListFetchMock = companyListFetch,
      companyCreateMock = companyCreate,
      companyUpdateMock = companyUpdate
    )
  }

  it should "dispatch CompanyCreateRequestAction when ADD command" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val offset = None
    val symbols = None
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      offset
    )
    actions.companyListFetch.expects(dispatch, offset, symbols).returning(action)
    dispatch.expects(action)
    
    val comp = createTestRenderer(<(CompanyPanel())(^.wrapped := props)()).root
    val bpProps = findComponentProps(comp, buttonsPanel)

    //then
    dispatch.expects(CompanyCreateRequestAction(create = true))
    
    //when
    bpProps.actions.onCommand(dispatch)(Buttons.ADD.command)
  }

  it should "dispatch CompanyUpdateRequestAction when EDIT command" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val offset = None
    val symbols = None
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      offset
    )
    actions.companyListFetch.expects(dispatch, offset, symbols).returning(action)
    dispatch.expects(action)

    val comp = createTestRenderer(<(CompanyPanel())(^.wrapped := props)()).root
    val bpProps = findComponentProps(comp, buttonsPanel)

    //then
    dispatch.expects(CompanyUpdateRequestAction(update = true))
    
    //when
    bpProps.actions.onCommand(dispatch)(Buttons.EDIT.command)
  }

  it should "dispatch CompanyCreateAction when onOk in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState(showCreatePopup = true)
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val offset = None
    val symbols = None
    val fetchAction = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      offset
    )
    actions.companyListFetch.expects(dispatch, offset, symbols).returning(fetchAction)
    dispatch.expects(fetchAction)

    val comp = createTestRenderer(<(CompanyPanel())(^.wrapped := props)()).root
    val createPopupProps = findComponentProps(comp, inputPopup)
    val text = "new comp"
    val action = CompanyCreateAction(
      FutureTask("Creating", Future.successful(CompanyResp(CompanyData(Some(1), text))))
    )
    actions.companyCreate.expects(dispatch, text).returning(action)

    //then
    dispatch.expects(action)
    
    //when
    createPopupProps.onOk(text)
  }

  it should "dispatch CompanyCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState(showCreatePopup = true)
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val offset = None
    val symbols = None
    val fetchAction = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      offset
    )
    actions.companyListFetch.expects(dispatch, offset, symbols).returning(fetchAction)
    dispatch.expects(fetchAction)

    val comp = createTestRenderer(<(CompanyPanel())(^.wrapped := props)()).root
    val createPopupProps = findComponentProps(comp, inputPopup)

    //then
    dispatch.expects(CompanyCreateRequestAction(create = false))
    
    //when
    createPopupProps.onCancel()
  }

  it should "dispatch CompanyUpdateAction when onOk in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      selectedId = Some(1),
      showEditPopup = true
    )
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val comp = createTestRenderer(<(CompanyPanel())(^.wrapped := props)()).root
    val editPopupProps = findComponentProps(comp, inputPopup)
    val text = "updated comp"
    val data = CompanyData(Some(1), text)
    val action = CompanyUpdateAction(
      FutureTask("Updating", Future.successful(CompanyResp(data)))
    )
    actions.companyUpdate.expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    editPopupProps.onOk(text)
  }

  it should "dispatch CompanyUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      selectedId = Some(1),
      showEditPopup = true
    )
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val comp = createTestRenderer(<(CompanyPanel())(^.wrapped := props)()).root
    val editPopupProps = findComponentProps(comp, inputPopup)

    //then
    dispatch.expects(CompanyUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "dispatch CompanySelectedAction when select row" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val offset = None
    val symbols = None
    val fetchAction = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      offset
    )
    actions.companyListFetch.expects(dispatch, offset, symbols).returning(fetchAction)
    dispatch.expects(fetchAction)

    val comp = createTestRenderer(<(CompanyPanel())(^.wrapped := props)()).root
    val tpProps = findComponentProps(comp, companyTablePanel)
    val companyId = 1

    //then
    dispatch.expects(CompanySelectedAction(companyId))

    //when
    tpProps.onChangeSelect(companyId)
  }

  it should "dispatch CompanyListFetchAction when select page" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val fetchAction = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      None
    )
    actions.companyListFetch.expects(dispatch, None, None).returning(fetchAction)
    dispatch.expects(fetchAction)

    val comp = createTestRenderer(<(CompanyPanel())(^.wrapped := props)()).root
    val tpProps = findComponentProps(comp, companyTablePanel)
    val offset = Some(10)
    val symbols = None
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      offset
    )
    actions.companyListFetch.expects(dispatch, offset, symbols)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    tpProps.onLoadData(offset, symbols)
  }

  it should "dispatch CompanyListFetchAction if empty dataList when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val comp = <(CompanyPanel())(^.wrapped := props)()
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      None
    )
    actions.companyListFetch.expects(dispatch, None, None)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    val renderer = createTestRenderer(comp)
    
    //cleanup
    renderer.unmount()
  }

  it should "not dispatch CompanyListFetchAction if non empty dataList when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val state = CompanyState(List(
      CompanyData(Some(1), "Test Company"),
      CompanyData(Some(2), "Test Company 2")
    ))
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val comp = <(CompanyPanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    val renderer = createTestRenderer(comp)

    //cleanup
    renderer.unmount()
  }

  it should "render component" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = new Actions
    val state = CompanyState(List(
      CompanyData(Some(1), "test comp 1"),
      CompanyData(Some(2), "test comp 2")
    ))
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val component = <(CompanyPanel())(^.wrapped := props)()
    
    //when
    val result = createTestRenderer(component).root
    
    //then
    assertCompanyPanel(result, props)
  }

  it should "render component and show create popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = new Actions
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      showCreatePopup = true
    )
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val component = <(CompanyPanel())(^.wrapped := props)()
    
    //when
    val result = createTestRenderer(component).root
    
    //then
    assertCompanyPanel(result, props)
  }

  it should "render component and show edit popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = new Actions
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      selectedId = Some(1),
      showEditPopup = true
    )
    val props = CompanyPanelProps(dispatch, actions.actions, state)
    val component = <(CompanyPanel())(^.wrapped := props)()
    
    //when
    val result = createTestRenderer(component).root
    
    //then
    assertCompanyPanel(result, props)
  }

  private def assertCompanyPanel(result: TestInstance, props: CompanyPanelProps): Unit = {
    val selectedData = props.data.dataList.find(_.id == props.data.selectedId)

    def assertComponents(resButtonsPanel: TestInstance,
                         tablePanel: TestInstance,
                         createPopup: Option[TestInstance],
                         editPopup: Option[TestInstance]): Assertion = {

      assertTestComponent(resButtonsPanel, buttonsPanel) {
        case ButtonsPanelProps(buttons, actions, dispatch, group, _) =>
          buttons shouldBe List(Buttons.ADD, Buttons.EDIT)
          actions.enabledCommands shouldBe {
            Set(Buttons.ADD.command) ++ selectedData.map(_ => Buttons.EDIT.command)
          }
          dispatch shouldBe props.dispatch
          group shouldBe false
      }
      assertTestComponent(tablePanel, companyTablePanel) {
        case CompanyTablePanelProps(data, _, _) =>
          data shouldBe props.data
      }

      createPopup.isDefined shouldBe props.data.showCreatePopup
      createPopup.foreach { cp =>
        assertTestComponent(cp, inputPopup) {
          case InputPopupProps(message, _, _, placeholder, initialValue) =>
            message shouldBe "Enter Company name:"
            placeholder shouldBe None
            initialValue shouldBe "New Company"
        }
      }
      
      editPopup.isEmpty shouldBe (selectedData.isEmpty && !props.data.showEditPopup)
      selectedData.foreach { data =>
        assertTestComponent(editPopup.get, inputPopup) {
          case InputPopupProps(message, _, _, placeholder, initialValue) =>
            message shouldBe "Enter new Company name:"
            placeholder shouldBe None
            initialValue shouldBe data.name
        }
      }
      Succeeded
    }
    
    inside(result.children.toList) {
      case List(bp, tp, createPopup) if props.data.showCreatePopup => assertComponents(bp, tp, Some(createPopup), None)
      case List(bp, tp, editPopup) if props.data.showEditPopup => assertComponents(bp, tp, None, Some(editPopup))
      case List(bp, tp) => assertComponents(bp, tp, None, None)
    }
  }
}
