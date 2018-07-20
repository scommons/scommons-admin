package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.company._
import scommons.admin.client.company.action._
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui._
import scommons.client.ui.page.PaginationPanel._
import scommons.client.ui.page._
import scommons.client.ui.popup._
import scommons.client.ui.table._

import scala.concurrent.Future

class CompanyPanelSpec extends TestSpec {

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

  it should "dispatch CompanySelectedAction when select row" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val tpProps = findComponentProps(comp, TablePanel)
    val row = TableRowData("1", List("1", "test comp 1"))

    //then
    dispatch.expects(CompanySelectedAction(row.id.toInt))
    
    //when
    tpProps.onSelect(row)
  }

  it should "dispatch CompanyListFetchAction when select page" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyPanel())(^.wrapped := props)())
    val ppProps = findComponentProps(comp, PaginationPanel)
    val page = 2
    val offset = Some(10)
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil))),
      offset
    )
    (actions.companyListFetch _).expects(dispatch, offset, None)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    ppProps.onPage(page)
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
    dispatch.expects(CompanyCreateRequestAction(create = false))
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
    dispatch.expects(CompanyUpdateRequestAction(update = false))
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

  it should "dispatch CompanyListFetchAction when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyPanelProps(dispatch, actions, state)
    val component = <(CompanyPanel())(^.wrapped := props)()
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil))),
      None
    )
    (actions.companyListFetch _).expects(dispatch, None, None)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    renderIntoDocument(component)
  }

  it should "not dispatch CompanyListFetchAction if non empty dataList when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState(List(
      CompanyData(Some(1), "test comp 1"),
      CompanyData(Some(2), "test comp 2")
    ))
    val props = CompanyPanelProps(dispatch, actions, state)
    val component = <(CompanyPanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    renderIntoDocument(component)
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

  it should "render component with selected row" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[CompanyActions]
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      selectedId = Some(1)
    )
    val props = CompanyPanelProps(dispatch, actions, state)
    val component = <(CompanyPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertCompanyPanel(result, props)
  }

  it should "render component with selected second page" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[CompanyActions]
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "test comp 1"),
        CompanyData(Some(2), "test comp 2")
      ),
      offset = Some(CompanyActions.listLimit),
      totalCount = Some(CompanyActions.listLimit + 5)
    )
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

  private def assertCompanyPanel(result: ComponentInstance, props: CompanyPanelProps): Unit = {
    val tableHeader = List(
      TableColumnData("Id"),
      TableColumnData("Company Name")
    )
    val tableRows = props.state.dataList.map { data =>
      val id = data.id.getOrElse(0).toString
      TableRowData(id, List(id, data.name))
    }
    val selectedData = props.state.dataList.find(_.id == props.state.selectedId)

    val limit = CompanyActions.listLimit
    val expectedTotalPages = toTotalPages(props.state.totalCount.getOrElse(0), limit)
    val expectedSelectedPage = math.min(expectedTotalPages, toPage(props.state.offset.getOrElse(0), limit))

    def assertComponents(buttonsPanel: ComponentInstance,
                         tablePanel: ComponentInstance,
                         paginationPanel: ComponentInstance,
                         createPopup: ComponentInstance,
                         editPopup: Option[ComponentInstance]): Assertion = {

      assertComponent(buttonsPanel, ButtonsPanel(), { bpProps: ButtonsPanelProps =>
        inside(bpProps) { case ButtonsPanelProps(buttons, actions, dispatch, group, _) =>
          buttons shouldBe List(Buttons.ADD, Buttons.EDIT)
          actions.enabledCommands shouldBe {
            Set(Buttons.ADD.command) ++ selectedData.map(_ => Buttons.EDIT.command)
          }
          dispatch shouldBe props.dispatch
          group shouldBe false
        }
      })
      assertComponent(tablePanel, TablePanel(), { tpProps: TablePanelProps =>
        inside(tpProps) { case TablePanelProps(header, rows, selectedIds, _) =>
          header shouldBe tableHeader
          rows shouldBe tableRows
          selectedIds shouldBe props.state.selectedId.map(_.toString).toSet
        }
      })
      assertComponent(paginationPanel, PaginationPanel(), { ppProps: PaginationPanelProps =>
        inside(ppProps) { case PaginationPanelProps(totalPages, selectedPage, _, alignment) =>
          totalPages shouldBe expectedTotalPages
          selectedPage shouldBe expectedSelectedPage
          alignment shouldBe PaginationAlignment.Centered
        }
      })
      assertComponent(createPopup, InputPopup(), { ppProps: InputPopupProps =>
        inside(ppProps) { case InputPopupProps(show, message, _, _, placeholder, initialValue) =>
          show shouldBe props.state.showCreatePopup
          message shouldBe "Enter Company name:"
          placeholder shouldBe None
          initialValue shouldBe "New Company"
        }
      })
      editPopup.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(editPopup.get, InputPopup(), { ppProps: InputPopupProps =>
          inside(ppProps) { case InputPopupProps(show, message, _, _, placeholder, initialValue) =>
            show shouldBe props.state.showEditPopup
            message shouldBe "Enter new Company name:"
            placeholder shouldBe None
            initialValue shouldBe data.name
          }
        })
      }
      Succeeded
    }
    
    assertDOMComponent(result, <.div()(), {
      case List(bp, tp, pp, createPopup) => assertComponents(bp, tp, pp, createPopup, None)
      case List(bp, tp, pp, createPopup, editPopup) => assertComponents(bp, tp, pp, createPopup, Some(editPopup))
    })
  }
}
