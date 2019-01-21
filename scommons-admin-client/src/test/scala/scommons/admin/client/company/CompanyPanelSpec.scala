package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.company._
import scommons.admin.client.company.CompanyActions._
import scommons.client.task.FutureTask
import scommons.client.ui._
import scommons.client.ui.popup._
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils

import scala.concurrent.Future

class CompanyPanelSpec extends TestSpec with ShallowRendererUtils {

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
    val state = CompanyState()
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
    val state = CompanyState()
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
      selectedId = Some(1)
    )
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, InputPopup)(1)
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
      selectedId = Some(1)
    )
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, InputPopup)(1)

    //then
    dispatch.expects(CompanyUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
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
                         createPopup: ShallowInstance,
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
        case CompanyTablePanelProps(dispatch, actions, data) =>
          dispatch shouldBe props.dispatch
          actions shouldBe props.actions
          data shouldBe props.data
      }
      
      assertComponent(createPopup, InputPopup) {
        case InputPopupProps(show, message, _, _, placeholder, initialValue) =>
          show shouldBe props.data.showCreatePopup
          message shouldBe "Enter Company name:"
          placeholder shouldBe None
          initialValue shouldBe "New Company"
      }
      editPopup.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(editPopup.get, InputPopup) {
          case InputPopupProps(show, message, _, _, placeholder, initialValue) =>
            show shouldBe props.data.showEditPopup
            message shouldBe "Enter new Company name:"
            placeholder shouldBe None
            initialValue shouldBe data.name
        }
      }
      Succeeded
    }
    
    assertNativeComponent(result, <.div()(), {
      case List(bp, tp, createPopup) => assertComponents(bp, tp, createPopup, None)
      case List(bp, tp, createPopup, editPopup) => assertComponents(bp, tp, createPopup, Some(editPopup))
    })
  }
}
