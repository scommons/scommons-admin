package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import org.joda.time.DateTime
import scommons.admin.client.api.system.user._
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.page.PaginationPanel._
import scommons.client.ui.page._
import scommons.client.ui.table._

class SystemUserTablePanelSpec extends TestSpec {

  it should "call onChangeSelect when select row" in {
    //given
    val onChangeSelect = mockFunction[Int, Unit]
    val state = SystemUserState()
    val props = getSystemUserTablePanelProps(state, onChangeSelect = onChangeSelect)
    val comp = shallowRender(<(SystemUserTablePanel())(^.wrapped := props)())
    val tpProps = findComponentProps(comp, TablePanel)
    val id = 1
    val row = TableRowData(id.toString, List("1", "test user 1"))
    
    //then
    onChangeSelect.expects(id)
    
    //when
    tpProps.onSelect(row)
  }

  it should "call onLoadData when select page" in {
    //given
    val onLoadData = mockFunction[Option[Int], Option[String], Unit]
    val state = SystemUserState()
    val props = getSystemUserTablePanelProps(state, onLoadData = onLoadData)
    val comp = shallowRender(<(SystemUserTablePanel())(^.wrapped := props)())
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
    val state = getSystemUserState
    val props = getSystemUserTablePanelProps(state)
    val component = <(SystemUserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemUserTablePanel(result, props)
  }

  it should "render component with selected user in the list" in {
    //given
    val state = getSystemUserState
    val props = getSystemUserTablePanelProps(state, selectedUserId = Some(state.dataList.head.userId))
    val component = <(SystemUserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemUserTablePanel(result, props)
  }

  ignore should "render component with selected user added to the list" in {
    //given
    val state = {
      val state = getSystemUserState
//      val details = state.userDetails.get
//      state.copy(userDetails = Some(details.copy(user = details.user.copy(id = Some(123)))))
      state
    }
    val props = getSystemUserTablePanelProps(state, selectedUserId = Some(123))
    val component = <(SystemUserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemUserTablePanel(result, props.copy(
//      data = state.copy(dataList = state.dataList :+ state.userDetails.get.user)
    ))
  }

  it should "render component without selected user" in {
    //given
    val state = getSystemUserState
    val props = getSystemUserTablePanelProps(state, selectedUserId = Some(123))
    val component = <(SystemUserTablePanel())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertSystemUserTablePanel(result, props)
  }

  it should "render component with selected second page" in {
    //given
    val state = getSystemUserState.copy(
      offset = Some(SystemUserActions.listLimit),
      totalCount = Some(SystemUserActions.listLimit + 5)
    )
    val props = getSystemUserTablePanelProps(state)
    val component = <(SystemUserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemUserTablePanel(result, props)
  }
  
  private def getSystemUserState: SystemUserState = {
    val user = SystemUserData(
      userId = 1,
      login = "test_login_1",
      lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
      updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
      createdAt = DateTime("2018-12-03T11:29:01.234Z"),
      version = 123
    )

    SystemUserState(
      dataList = List(
        user,
        SystemUserData(
          userId = 2,
          login = "test_login_2",
          lastLoginDate = None,
          updatedAt = DateTime("2018-12-01T10:29:01.234Z"),
          createdAt = DateTime("2018-12-02T11:29:01.234Z"),
          version = 124
        )
      )
    )
  }

  private def getSystemUserTablePanelProps(data: SystemUserState,
                                           selectedUserId: Option[Int] = None,
                                           onChangeSelect: Int => Unit = _ => (),
                                           onLoadData: (Option[Int], Option[String]) => Unit = (_, _) => ()
                                          ): SystemUserTablePanelProps = {

    SystemUserTablePanelProps(
      data = data,
      selectedUserId = selectedUserId,
      onChangeSelect = onChangeSelect,
      onLoadData = onLoadData
    )
  }
  
  private def assertSystemUserTablePanel(result: ComponentInstance, props: SystemUserTablePanelProps): Unit = {
    val tableHeader = List(
      TableColumnData("Login"),
      TableColumnData("Logged-in at"),
      TableColumnData("Created at"),
      TableColumnData("Updated at")
    )
    val tableRows = props.data.dataList.map { data =>
      val id = data.userId.toString
      TableRowData(id, List(
        data.login,
        data.lastLoginDate.map(_.toString()).getOrElse(""),
        data.createdAt.toString(),
        data.updatedAt.toString()
      ))
    }
    
    val limit = SystemUserActions.listLimit
    val expectedTotalPages = toTotalPages(props.data.totalCount.getOrElse(0), limit)
    val expectedSelectedPage = math.min(expectedTotalPages, toPage(props.data.offset.getOrElse(0), limit))

    assertDOMComponent(result, <.div()(), { case List(tablePanel, paginationPanel) =>
      assertComponent(tablePanel, TablePanel) {
        case TablePanelProps(header, rows, selectedIds, _) =>
          header shouldBe tableHeader
          rows shouldBe tableRows
          selectedIds shouldBe props.selectedUserId.map(_.toString).toSet
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
