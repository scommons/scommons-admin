package scommons.admin.client.company

import scommons.admin.client.api.company._
import scommons.admin.client.company.CompanyTablePanel._
import scommons.client.ui.page.PaginationPanel._
import scommons.client.ui.page._
import scommons.client.ui.table._
import scommons.react.test._

class CompanyTablePanelSpec extends TestSpec with TestRendererUtils {

  CompanyTablePanel.tablePanel = mockUiComponent("TablePanel")
  CompanyTablePanel.paginationPanel = mockUiComponent("PaginationPanel")

  it should "call onChangeSelect when select row" in {
    //given
    val state = CompanyState()
    val onChangeSelect = mockFunction[Int, Unit]
    val props = CompanyTablePanelProps(state, onChangeSelect = onChangeSelect, (_, _) => ())
    val comp = createTestRenderer(<(CompanyTablePanel())(^.wrapped := props)()).root
    val tpProps = findComponentProps(comp, tablePanel)
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
    val comp = createTestRenderer(<(CompanyTablePanel())(^.wrapped := props)()).root
    val ppProps = findComponentProps(comp, paginationPanel)
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
    val result = createTestRenderer(<(CompanyTablePanel())(^.wrapped := props)()).root
    
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
    val result = createTestRenderer(<(CompanyTablePanel())(^.wrapped := props)()).root
    
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
    val result = createTestRenderer(<(CompanyTablePanel())(^.wrapped := props)()).root
    
    //then
    assertCompanyTablePanel(result, props)
  }
  
  private def assertCompanyTablePanel(result: TestInstance, props: CompanyTablePanelProps): Unit = {
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

    inside(result.children.toList) { case List(resTablePanel, resPaginationPanel) =>
      assertTestComponent(resTablePanel, tablePanel) {
        case TablePanelProps(header, rows, keyExtractor, rowClassExtractor, cellRenderer, selectedIds, _) =>
          header shouldBe tableHeader
          rows shouldBe tableRows
          keyExtractor(rows.head) shouldBe rows.head.id
          rowClassExtractor shouldBe TablePanelProps.defaultRowClassExtractor
          cellRenderer(rows.head, 0) shouldBe rows.head.cells.head
          selectedIds shouldBe props.data.selectedId.map(_.toString).toSet
      }
      assertTestComponent(resPaginationPanel, paginationPanel) {
        case PaginationPanelProps(totalPages, selectedPage, _, alignment) =>
          totalPages shouldBe expectedTotalPages
          selectedPage shouldBe expectedSelectedPage
          alignment shouldBe PaginationAlignment.Centered
      }
    }
  }
}
