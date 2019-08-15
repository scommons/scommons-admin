package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.company._
import scommons.admin.client.company.CompanyActions._
import scommons.client.ui._
import scommons.client.ui.popup._
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.{ShallowRendererUtils, TestRendererUtils}

import scala.concurrent.Future

class CompanyPanelSpec extends TestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "dispatch CompanyCreateRequestAction when ADD command" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val bpProps = findComponentProps(comp, ButtonsPanel)

    //then
    dispatch.expects(CompanyCreateRequestAction(create = true))
    
    //when
    bpProps.actions.onCommand(dispatch)(Buttons.ADD.command)
  }

  it should "dispatch CompanyUpdateRequestAction when EDIT command" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val bpProps = findComponentProps(comp, ButtonsPanel)

    //then
    dispatch.expects(CompanyUpdateRequestAction(update = true))
    
    //when
    bpProps.actions.onCommand(dispatch)(Buttons.EDIT.command)
  }

  it should "dispatch CompanyCreateAction when onOk in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState(showCreatePopup = true)
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, InputPopup)
    val text = "new comp"
    val action = CompanyCreateAction(
      FutureTask("Creating", Future.successful(CompanyResp(CompanyData(Some(1), text))))
    )
    (actions.companyCreate _).expects(dispatch, text)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    createPopupProps.onOk(text)
  }

  it should "dispatch CompanyCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState(showCreatePopup = true)
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, InputPopup)

    //then
    dispatch.expects(CompanyCreateRequestAction(create = false))
    
    //when
    createPopupProps.onCancel()
  }

  it should "dispatch CompanyUpdateAction when onOk in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      selectedId = Some(1),
      showEditPopup = true
    )
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val editPopupProps = findComponentProps(comp, InputPopup)
    val text = "updated comp"
    val data = CompanyData(Some(1), text)
    val action = CompanyUpdateAction(
      FutureTask("Updating", Future.successful(CompanyResp(data)))
    )
    (actions.companyUpdate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    editPopupProps.onOk(text)
  }

  it should "dispatch CompanyUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      selectedId = Some(1),
      showEditPopup = true
    )
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val editPopupProps = findComponentProps(comp, InputPopup)

    //then
    dispatch.expects(CompanyUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "dispatch CompanySelectedAction when select row" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val tpProps = findComponentProps(comp, CompanyTablePanel)
    val companyId = 1

    //then
    dispatch.expects(CompanySelectedAction(companyId))

    //when
    tpProps.onChangeSelect(companyId)
  }

  it should "dispatch CompanyListFetchAction when select page" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val tpProps = findComponentProps(comp, CompanyTablePanel)
    val offset = Some(10)
    val symbols = None
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      offset
    )
    (actions.companyListFetch _).expects(dispatch, offset, symbols)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    tpProps.onLoadData(offset, symbols)
  }

  it should "dispatch CompanyListFetchAction if empty dataList when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = <(CompanyPanel())(^.wrapped := props)()
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      None
    )
    (actions.companyListFetch _).expects(dispatch, None, None)
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
    val actions = mock[CompanyActions]
    val state = CompanyState(List(
      CompanyData(Some(1), "Test Company"),
      CompanyData(Some(2), "Test Company 2")
    ))
    val props = CompanyPanelProps(dispatch, actions, state)
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
    val actions = mock[CompanyActions]
    val state = CompanyState(List(
      CompanyData(Some(1), "test comp 1"),
      CompanyData(Some(2), "test comp 2")
    ))
    val props = CompanyPanelProps(dispatch, actions, state)
    val component = <(CompanyPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertCompanyPanel(result, props)
  }

  it should "render component and show create popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[CompanyActions]
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      showCreatePopup = true
    )
    val props = CompanyPanelProps(dispatch, actions, state)
    val component = <(CompanyPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertCompanyPanel(result, props)
  }

  it should "render component and show edit popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[CompanyActions]
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      selectedId = Some(1),
      showEditPopup = true
    )
    val props = CompanyPanelProps(dispatch, actions, state)
    val component = <(CompanyPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertCompanyPanel(result, props)
  }

  private def assertCompanyPanel(result: ShallowInstance, props: CompanyPanelProps): Unit = {
    val selectedData = props.data.dataList.find(_.id == props.data.selectedId)

    def assertComponents(buttonsPanel: ShallowInstance,
                         tablePanel: ShallowInstance,
                         createPopup: Option[ShallowInstance],
                         editPopup: Option[ShallowInstance]): Assertion = {

      assertComponent(buttonsPanel, ButtonsPanel) {
        case ButtonsPanelProps(buttons, actions, dispatch, group, _) =>
          buttons shouldBe List(Buttons.ADD, Buttons.EDIT)
          actions.enabledCommands shouldBe {
            Set(Buttons.ADD.command) ++ selectedData.map(_ => Buttons.EDIT.command)
          }
          dispatch shouldBe props.dispatch
          group shouldBe false
      }
      assertComponent(tablePanel, CompanyTablePanel) {
        case CompanyTablePanelProps(data, _, _) =>
          data shouldBe props.data
      }

      createPopup.isDefined shouldBe props.data.showCreatePopup
      createPopup.foreach { cp =>
        assertComponent(cp, InputPopup) {
          case InputPopupProps(message, _, _, placeholder, initialValue) =>
            message shouldBe "Enter Company name:"
            placeholder shouldBe None
            initialValue shouldBe "New Company"
        }
      }
      
      editPopup.isEmpty shouldBe (selectedData.isEmpty && !props.data.showEditPopup)
      selectedData.foreach { data =>
        assertComponent(editPopup.get, InputPopup) {
          case InputPopupProps(message, _, _, placeholder, initialValue) =>
            message shouldBe "Enter new Company name:"
            placeholder shouldBe None
            initialValue shouldBe data.name
        }
      }
      Succeeded
    }
    
    assertNativeComponent(result, <.>()(), { children: List[ShallowInstance] =>
      children match {
        case List(bp, tp, createPopup) if props.data.showCreatePopup => assertComponents(bp, tp, Some(createPopup), None)
        case List(bp, tp, editPopup) if props.data.showEditPopup => assertComponents(bp, tp, None, Some(editPopup))
        case List(bp, tp) => assertComponents(bp, tp, None, None)
      }
    })
  }
}
