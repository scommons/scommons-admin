package scommons.admin.client.company

import scommons.admin.client.api.company._
import scommons.client.ui.page.PaginationPanel._
import scommons.client.ui.page._
import scommons.client.ui.table._
import scommons.react._
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils

class CompanyTablePanelSpec extends TestSpec with ShallowRendererUtils {

  it should "call onChangeSelect when select row" in {
    //given
    val state = CompanyState()
    val onChangeSelect = mockFunction[Int, Unit]
    val props = CompanyTablePanelProps(state, onChangeSelect = onChangeSelect, (_, _) => ())
    val comp = shallowRender(<(CompanyTablePanel())(^.wrapped := props)())
    val tpProps = findComponentProps(comp, TablePanel)
    val companyId = 1
    val row = TableRowData(s"$companyId", List("1", "test user 1"))

    //then
    onChangeSelect.expects(companyId)

    //when
    tpProps.onSelect(row)
  }

  it should "call onLoadData when select page" in {
    //given
    val onLoadData = mockFunction[Option[Int], Option[String], Unit]
    val state = CompanyState()
    val props = CompanyTablePanelProps(state, _ => (), onLoadData = onLoadData)
    val comp = shallowRender(<(CompanyTablePanel())(^.wrapped := props)())
    val ppProps = findComponentProps(comp, PaginationPanel)
    val page = 2
    val offset = Some(10)

    //then
    onLoadData.expects(offset, None)

    //when
    ppProps.onPage(page)
  }

  it should "render component" in {
    //given
    val state = CompanyState(List(
      CompanyData(Some(1), "Test Company"),
      CompanyData(Some(2), "Test Company 2")
    ))
    val props = CompanyTablePanelProps(state, _ => (), (_, _) => ())
    
    //when
    val result = shallowRender(<(CompanyTablePanel())(^.wrapped := props)())
    
    //then
    assertCompanyTablePanel(result, props)
  }

  it should "render component with selected row" in {
    //given
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "Test Company"),
        CompanyData(Some(2), "Test Company 2")
      ),
      selectedId = Some(1)
    )
    val props = CompanyTablePanelProps(state, _ => (), (_, _) => ())
    
    //when
    val result = shallowRender(<(CompanyTablePanel())(^.wrapped := props)())
    
    //then
    assertCompanyTablePanel(result, props)
  }

  it should "render component with selected second page" in {
    //given
    val state = CompanyState(
      dataList = List(
        CompanyData(Some(1), "Test Company"),
        CompanyData(Some(2), "Test Company 2")
      ),
      offset = Some(CompanyActions.listLimit),
      totalCount = Some(CompanyActions.listLimit + 5)
    )
    val props = CompanyTablePanelProps(state, _ => (), (_, _) => ())
    
    //when
    val result = shallowRender(<(CompanyTablePanel())(^.wrapped := props)())
    
    //then
    assertCompanyTablePanel(result, props)
  }
  
  private def assertCompanyTablePanel(result: ShallowInstance, props: CompanyTablePanelProps): Unit = {
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

    assertNativeComponent(result, <.>()(), { case List(tablePanel, paginationPanel) =>
      assertComponent(tablePanel, TablePanel) {
        case TablePanelProps(header, rows, selectedIds, _) =>
          header shouldBe tableHeader
          rows shouldBe tableRows
          selectedIds shouldBe props.data.selectedId.map(_.toString).toSet
      }
      assertComponent(paginationPanel, PaginationPanel) {
        case PaginationPanelProps(totalPages, selectedPage, _, alignment) =>
          totalPages shouldBe expectedTotalPages
          selectedPage shouldBe expectedSelectedPage
          alignment shouldBe PaginationAlignment.Centered
      }
    })
  }
}
