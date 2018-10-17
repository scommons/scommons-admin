package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions.{UserFetchAction, UserListFetchAction}
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.page.PaginationPanel._
import scommons.client.ui.page._
import scommons.client.ui.table._

import scala.concurrent.Future

class UserTablePanelSpec extends TestSpec {

  it should "dispatch UserFetchAction when select row" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val respData = mock[UserDetailsData]
    val state = UserState()
    val props = UserTablePanelProps(dispatch, actions, state)
    val comp = shallowRender(<(UserTablePanel())(^.wrapped := props)())
    val tpProps = findComponentProps(comp, TablePanel)
    val id = 1
    val row = TableRowData(id.toString, List("1", "test user 1"))
    val action = UserFetchAction(
      FutureTask("Fetching", Future.successful(UserDetailsResp(respData)))
    )
    (actions.userFetch _).expects(dispatch, id)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    tpProps.onSelect(row)
  }

  it should "dispatch UserListFetchAction when select page" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val state = UserState()
    val props = UserTablePanelProps(dispatch, actions, state)
    val comp = shallowRender(<(UserTablePanel())(^.wrapped := props)())
    val ppProps = findComponentProps(comp, PaginationPanel)
    val page = 2
    val offset = Some(10)
    val action = UserListFetchAction(
      FutureTask("Fetching", Future.successful(UserListResp(Nil, None))),
      offset
    )
    (actions.userListFetch _).expects(dispatch, offset, None)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    ppProps.onPage(page)
  }

  it should "dispatch UserListFetchAction when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val state = UserState()
    val props = UserTablePanelProps(dispatch, actions, state)
    val component = <(UserTablePanel())(^.wrapped := props)()
    val action = UserListFetchAction(
      FutureTask("Fetching", Future.successful(UserListResp(Nil, None))),
      None
    )
    (actions.userListFetch _).expects(dispatch, None, None)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    renderIntoDocument(component)
  }

  it should "not dispatch UserListFetchAction if non empty dataList when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val company = UserCompanyData(1, "Test Company")
    val state = UserState(List(
      UserData(Some(1), company, "test user 1", "test", active = true),
      UserData(Some(2), company, "test user 2", "test", active = true)
    ))
    val props = UserTablePanelProps(dispatch, actions, state)
    val component = <(UserTablePanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    renderIntoDocument(component)
  }

  it should "render component" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserActions]
    val company = UserCompanyData(1, "Test Company")
    val state = UserState(List(
      UserData(Some(1), company, "test user 1", "test", active = true),
      UserData(Some(2), company, "test user 2", "test", active = true)
    ))
    val props = UserTablePanelProps(dispatch, actions, state)
    val component = <(UserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserTablePanel(result, props)
  }

  it should "render component with selected row" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserActions]
    val company = UserCompanyData(1, "Test Company")
    val state = UserState(
      dataList = List(
        UserData(Some(1), company, "test user 1", "test", active = true),
        UserData(Some(2), company, "test user 2", "test", active = true)
      ),
      selected = Some(UserDetailsData(
        user = UserData(
          id = Some(1),
          company = UserCompanyData(1, "Test Company"),
          login = "test_login",
          password = "test",
          active = true
        ),
        profile = UserProfileData(
          email = "test@email.com",
          firstName = "Firstname",
          lastName = "Lastname",
          phone = Some("0123 456 789")
        )
      ))
    )
    val props = UserTablePanelProps(dispatch, actions, state)
    val component = <(UserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserTablePanel(result, props)
  }

  it should "render component with selected second page" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserActions]
    val company = UserCompanyData(1, "Test Company")
    val state = UserState(
      dataList = List(
        UserData(Some(1), company, "test user 1", "test", active = true),
        UserData(Some(2), company, "test user 2", "test", active = true)
      ),
      offset = Some(UserActions.listLimit),
      totalCount = Some(UserActions.listLimit + 5)
    )
    val props = UserTablePanelProps(dispatch, actions, state)
    val component = <(UserTablePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserTablePanel(result, props)
  }
  
  private def assertUserTablePanel(result: ComponentInstance, props: UserTablePanelProps): Unit = {
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

    assertDOMComponent(result, <.div()(), { case List(tablePanel, paginationPanel) =>
      assertComponent(tablePanel, TablePanel) {
        case TablePanelProps(header, rows, selectedIds, _) =>
          header shouldBe tableHeader
          rows shouldBe tableRows
          selectedIds shouldBe props.data.selected.flatMap(_.user.id).map(_.toString).toSet
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
