package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.ReactDOM
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest.{Assertion, Succeeded}
import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions.{UserFetchAction, UserListFetchAction}
import scommons.client.task.FutureTask
import scommons.client.test.AsyncTestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.test.util.TestDOMUtils.findReactElement
import scommons.client.ui.page.PaginationPanel._
import scommons.client.ui.page._
import scommons.client.ui.table._

import scala.concurrent.Future

class UserTablePanelSpec extends AsyncTestSpec {

  it should "dispatch UserFetchAction and call onChangeSelect when select row" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    var selectedId: Option[Int] = None
    val onChangeSelect = { id: Option[Int] =>
      selectedId = id
    }
    val respData = mock[UserDetailsData]
    val state = UserState()
    val props = getUserTablePanelProps(dispatch, actions, state, onChangeSelect = onChangeSelect)
    val comp = shallowRender(<(UserTablePanel())(^.wrapped := props)())
    val tpProps = findComponentProps(comp, TablePanel)
    val id = 1
    val row = TableRowData(id.toString, List("1", "test user 1"))
    val action = UserFetchAction(
      FutureTask("Fetching", Future.successful(UserDetailsResp(respData)))
    )
    (actions.userFetch _).expects(dispatch, id).returning(action)
    dispatch.expects(action)
    
    //when
    tpProps.onSelect(row)
    
    //then
    eventually {
      selectedId shouldBe Some(id)
    }
  }

  it should "dispatch UserListFetchAction and call onChangeSelect when select page" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    var selectedId: Option[Int] = Some(1)
    val onChangeSelect = { id: Option[Int] =>
      selectedId = id
    }
    val state = UserState()
    val props = getUserTablePanelProps(dispatch, actions, state, onChangeSelect = onChangeSelect)
    val comp = shallowRender(<(UserTablePanel())(^.wrapped := props)())
    val ppProps = findComponentProps(comp, PaginationPanel)
    val page = 2
    val offset = Some(10)
    val action = UserListFetchAction(
      FutureTask("Fetching", Future.successful(UserListResp(Nil, None))),
      offset
    )
    (actions.userListFetch _).expects(dispatch, offset, None).returning(action)
    dispatch.expects(action)
    
    //when
    ppProps.onPage(page)

    //then
    eventually {
      selectedId shouldBe None
    }
  }

  it should "dispatch UserListFetchAction and UserFetchAction when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val respData = mock[UserDetailsData]
    val state = UserState()
    val userId = 11
    val props = getUserTablePanelProps(dispatch, actions, state, selectedUserId = Some(userId))
    val component = <(UserTablePanel())(^.wrapped := props)()
    val listFetchAction = UserListFetchAction(
      FutureTask("Fetching Users", Future.successful(UserListResp(Nil, None))),
      None
    )
    val fetchAction = UserFetchAction(
      FutureTask("Fetching User", Future.successful(UserDetailsResp(respData)))
    )
    (actions.userListFetch _).expects(dispatch, None, None).returning(listFetchAction)
    (actions.userFetch _).expects(dispatch, userId).returning(fetchAction)

    //then
    dispatch.expects(listFetchAction)
    dispatch.expects(fetchAction)

    //when
    renderIntoDocument(component)

    Succeeded
  }

  it should "not dispatch UserListFetchAction if non empty dataList when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val company = UserCompanyData(1, "Test Company")
    val state = UserState(dataList = List(
      UserData(Some(1), company, "test user 1", "test", active = true),
      UserData(Some(2), company, "test user 2", "test", active = true)
    ))
    val props = getUserTablePanelProps(dispatch, actions, state)
    val component = <(UserTablePanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    renderIntoDocument(component)

    Succeeded
  }

  it should "not dispatch UserFetchAction if selected user hasn't changed when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val state = getUserState
    val userId = state.userDetails.get.user.id.get
    val props = getUserTablePanelProps(dispatch, actions, state, selectedUserId = Some(userId))
    val component = <(UserTablePanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    renderIntoDocument(component)

    Succeeded
  }

  it should "dispatch UserFetchAction when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val respData = mock[UserDetailsData]
    val state = getUserState
    val userId = state.userDetails.get.user.id.get
    val prevProps = getUserTablePanelProps(dispatch, actions, state, selectedUserId = Some(userId))
    val comp = renderIntoDocument(<(UserTablePanel())(^.wrapped := prevProps)())
    val containerElement = findReactElement(comp).parentNode
    val newUserId = 123
    val props = prevProps.copy(
      selectedUserId = Some(newUserId)
    )
    val action = UserFetchAction(
      FutureTask("Fetching User", Future.successful(UserDetailsResp(respData)))
    )
    (actions.userFetch _).expects(dispatch, newUserId).returning(action)

    //then
    dispatch.expects(action)

    //when
    ReactDOM.render(<(UserTablePanel())(^.wrapped := props)(), containerElement)

    Succeeded
  }

  it should "not dispatch UserFetchAction if selected user hasn't changed when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val state = getUserState
    val userId = state.userDetails.get.user.id.get
    val prevProps = getUserTablePanelProps(dispatch, actions, state, selectedUserId = Some(userId))
    val comp = renderIntoDocument(<(UserTablePanel())(^.wrapped := prevProps)())
    val containerElement = findReactElement(comp).parentNode
    val props = prevProps.copy(
      data = state.copy(dataList = Nil)
    )

    //then
    dispatch.expects(*).never()

    //when
    ReactDOM.render(<(UserTablePanel())(^.wrapped := props)(), containerElement)

    Succeeded
  }

  it should "render component" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserActions]
    val state = getUserState.copy(userDetails = None)
    val props = getUserTablePanelProps(dispatch, actions, state)
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
    val state = getUserState
    val props = getUserTablePanelProps(dispatch, actions, state, selectedUserId = Some(1))
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
    val state = getUserState.copy(
      userDetails = None,
      offset = Some(UserActions.listLimit),
      totalCount = Some(UserActions.listLimit + 5)
    )
    val props = getUserTablePanelProps(dispatch, actions, state)
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
  
  private def getUserTablePanelProps(dispatch: Dispatch,
                                     actions: UserActions,
                                     data: UserState,
                                     selectedUserId: Option[Int] = None,
                                     onChangeSelect: Option[Int] => Unit = _ => ()): UserTablePanelProps = {

    UserTablePanelProps(
      dispatch = dispatch,
      actions = actions,
      data = data,
      selectedUserId = selectedUserId,
      onChangeSelect = onChangeSelect
    )
  }
  
  private def assertUserTablePanel(result: ComponentInstance, props: UserTablePanelProps): Assertion = {
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
          selectedIds shouldBe props.data.userDetails.flatMap(_.user.id).map(_.toString).toSet
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
