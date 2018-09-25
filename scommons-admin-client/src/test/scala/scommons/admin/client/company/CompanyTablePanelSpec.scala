package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.company._
import scommons.admin.client.company.CompanyActions._
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.page.PaginationPanel._
import scommons.client.ui.page._
import scommons.client.ui.table._

import scala.concurrent.Future

class CompanyTablePanelSpec extends TestSpec {

  it should "dispatch CompanySelectedAction when select row" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyTablePanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyTablePanel())(^.wrapped := props)())
    val tpProps = findComponentProps(comp, TablePanel)
    val row = TableRowData("1", List("1", "test user 1"))
    
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
    val props = CompanyTablePanelProps(dispatch, actions, state)
    val comp = shallowRender(<(CompanyTablePanel())(^.wrapped := props)())
    val ppProps = findComponentProps(comp, PaginationPanel)
    val page = 2
    val offset = Some(10)
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
      offset
    )
    (actions.companyListFetch _).expects(dispatch, offset, None)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    ppProps.onPage(page)
  }

  it should "dispatch CompanyListFetchAction when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CompanyActions]
    val state = CompanyState()
    val props = CompanyTablePanelProps(dispatch, actions, state)
    val component = <(CompanyTablePanel())(^.wrapped := props)()
    val action = CompanyListFetchAction(
      FutureTask("Fetching", Future.successful(CompanyListResp(Nil, None))),
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
      CompanyData(Some(1), "Test Company"),
      CompanyData(Some(2), "Test Company 2")
    ))
    val props = CompanyTablePanelProps(dispatch, actions, state)
    val component = <(CompanyTablePanel())(^.wrapped := props)()

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
      CompanyData(Some(1), "Test Company"),
      CompanyData(Some(2), "Test Company 2")
    ))
    val props = CompanyTablePanelProps(dispatch, actions, state)
    val component = <(CompanyTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertCompanyTablePanel(result, props)
  }

  it should "render component with selected row" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[CompanyActions]
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "Test Company"),
        CompanyData(Some(2), "Test Company 2")
      ),
      selectedId = Some(1)
    )
    val props = CompanyTablePanelProps(dispatch, actions, state)
    val component = <(CompanyTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertCompanyTablePanel(result, props)
  }

  it should "render component with selected second page" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[CompanyActions]
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "Test Company"),
        CompanyData(Some(2), "Test Company 2")
      ),
      offset = Some(CompanyActions.listLimit),
      totalCount = Some(CompanyActions.listLimit + 5)
    )
    val props = CompanyTablePanelProps(dispatch, actions, state)
    val component = <(CompanyTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertCompanyTablePanel(result, props)
  }
  
  private def assertCompanyTablePanel(result: ComponentInstance, props: CompanyTablePanelProps): Unit = {
    val tableHeader = List(
      TableColumnData("Id"),
      TableColumnData("Company Name")
    )
    val tableRows = props.data.dataList.map { data =>
      val id = data.id.getOrElse(0).toString
      TableRowData(id, List(id, data.name))
    }
    
    val limit = CompanyActions.listLimit
    val expectedTotalPages = toTotalPages(props.data.totalCount.getOrElse(0), limit)
    val expectedSelectedPage = math.min(expectedTotalPages, toPage(props.data.offset.getOrElse(0), limit))

    assertDOMComponent(result, <.div()(), { case List(tablePanel, paginationPanel) =>
      assertComponent(tablePanel, TablePanel(), { tpProps: TablePanelProps =>
        inside(tpProps) { case TablePanelProps(header, rows, selectedIds, _) =>
          header shouldBe tableHeader
          rows shouldBe tableRows
          selectedIds shouldBe props.data.selectedId.map(_.toString).toSet
        }
      })
      assertComponent(paginationPanel, PaginationPanel(), { ppProps: PaginationPanelProps =>
        inside(ppProps) { case PaginationPanelProps(totalPages, selectedPage, _, alignment) =>
          totalPages shouldBe expectedTotalPages
          selectedPage shouldBe expectedSelectedPage
          alignment shouldBe PaginationAlignment.Centered
        }
      })
    })
  }
}
