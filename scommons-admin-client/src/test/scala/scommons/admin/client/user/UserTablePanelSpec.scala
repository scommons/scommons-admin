package scommons.admin.client.user

import scommons.admin.client.api.user._
import scommons.client.ui.page.PaginationPanel._
import scommons.client.ui.page._
import scommons.client.ui.table._
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils

class UserTablePanelSpec extends TestSpec with ShallowRendererUtils {

  it should "call onChangeSelect when select row" in {
    //given
    val onChangeSelect = mockFunction[Int, Unit]
    val state = UserState()
    val props = getUserTablePanelProps(state, onChangeSelect = onChangeSelect)
    val comp = shallowRender(<(UserTablePanel())(^.wrapped := props)())
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
    val state = UserState()
    val props = getUserTablePanelProps(state, onLoadData = onLoadData)
    val comp = shallowRender(<(UserTablePanel())(^.wrapped := props)())
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
    val state = getUserState.copy(userDetails = None)
    val props = getUserTablePanelProps(state)
    val component = <(UserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserTablePanel(result, props)
  }

  it should "render component with selected user in the list" in {
    //given
    val state = getUserState
    val props = getUserTablePanelProps(state, selectedUserId = Some(state.dataList.head.id.get))
    val component = <(UserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserTablePanel(result, props)
  }

  it should "render component with selected user added to the list" in {
    //given
    val state = {
      val state = getUserState
      val details = state.userDetails.get
      state.copy(userDetails = Some(details.copy(user = details.user.copy(id = Some(123)))))
    }
    val props = getUserTablePanelProps(state, selectedUserId = Some(123))
    val component = <(UserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserTablePanel(result, props.copy(
      data = state.copy(dataList = state.dataList :+ state.userDetails.get.user)
    ))
  }

  it should "render component without selected user" in {
    //given
    val state = getUserState
    val props = getUserTablePanelProps(state, selectedUserId = Some(123))
    val component = <(UserTablePanel())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertUserTablePanel(result, props)
  }

  it should "render component with selected second page" in {
    //given
    val state = getUserState.copy(
      userDetails = None,
      offset = Some(UserActions.listLimit),
      totalCount = Some(UserActions.listLimit + 5)
    )
    val props = getUserTablePanelProps(state)
    val component = <(UserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserTablePanel(result, props)
  }
  
  private def getUserState: UserState = {
    val company = UserCompanyData(1, "Test Company")
    val user = UserData(Some(1), company, "test_login_1", "test", active = true)
    
    UserState(
      dataList = List(
        user,
        UserData(Some(2), company, "test_login_2", "test", active = true)
      ),
      userDetails = Some(UserDetailsData(
        user = user,
        profile = UserProfileData(
          email = "test@email.com",
          firstName = "Firstname",
          lastName = "Lastname",
          phone = Some("0123 456 789")
        )
      ))
    )
  }
  
  private def getUserTablePanelProps(data: UserState,
                                     selectedUserId: Option[Int] = None,
                                     onChangeSelect: Int => Unit = _ => (),
                                     onLoadData: (Option[Int], Option[String]) => Unit = (_, _) => ()
                                    ): UserTablePanelProps = {

    UserTablePanelProps(
      data = data,
      selectedUserId = selectedUserId,
      onChangeSelect = onChangeSelect,
      onLoadData = onLoadData
    )
  }
  
  private def assertUserTablePanel(result: ShallowInstance, props: UserTablePanelProps): Unit = {
    val tableHeader = List(
      TableColumnData("Login"),
      TableColumnData("Active"),
      TableColumnData("Logged-in at"),
      TableColumnData("Company"),
      TableColumnData("Updated at")
    )
    val tableRows = props.data.dataList.map { data =>
      val id = data.id.getOrElse(0).toString
      TableRowData(id, List(
        data.login,
        data.active.toString,
        data.lastLoginDate.map(_.toString()).getOrElse(""),
        data.company.name,
        data.updatedAt.map(_.toString()).getOrElse("")
      ))
    }
    
    val limit = UserActions.listLimit
    val expectedTotalPages = toTotalPages(props.data.totalCount.getOrElse(0), limit)
    val expectedSelectedPage = math.min(expectedTotalPages, toPage(props.data.offset.getOrElse(0), limit))

    assertNativeComponent(result, <.div()(), { case List(tablePanel, paginationPanel) =>
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
