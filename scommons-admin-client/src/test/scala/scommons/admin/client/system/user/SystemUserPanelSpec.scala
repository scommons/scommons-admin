package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.ReactDOM
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.joda.time.DateTime
import org.scalatest._
import scommons.admin.client.api.system.user._
import scommons.admin.client.system.user.SystemUserActions._
import scommons.client.task.FutureTask
import scommons.client.test.AsyncTestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.test.util.TestDOMUtils.findReactElement

import scala.concurrent.Future

class SystemUserPanelSpec extends AsyncTestSpec {

  it should "dispatch SystemUserListFetchAction when load data" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemUserActions]
    val systemId = 123
    val props = getSystemUserPanelProps(dispatch, actions = actions, selectedSystemId = Some(systemId))
    val comp = shallowRender(<(SystemUserPanel())(^.wrapped := props)())
    val tablePanelProps = findComponentProps(comp, SystemUserTablePanel)
    val offset = Some(10)
    val symbols = Some("test")
    val action = SystemUserListFetchAction(
      FutureTask("Fetching", Future.successful(SystemUserListResp(Nil, None))), systemId, offset
    )
    (actions.systemUserListFetch _).expects(dispatch, systemId, offset, symbols).returning(action)

    //then
    dispatch.expects(action)
    
    //when
    tablePanelProps.onLoadData(offset, symbols)
    
    Succeeded
  }

  it should "dispatch actions when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemUserActions]
    val systemId = 123
    val props = getSystemUserPanelProps(dispatch, actions = actions, selectedSystemId = Some(systemId))
    val component = <(SystemUserPanel())(^.wrapped := props)()
    val listFetchAction = SystemUserListFetchAction(
      FutureTask("Fetching SystemUsers", Future.successful(SystemUserListResp(Nil, None))), systemId, None
    )
    (actions.systemUserListFetch _).expects(dispatch, systemId, None, None).returning(listFetchAction)

    //then
    dispatch.expects(listFetchAction)

    //when
    renderIntoDocument(component)

    Succeeded
  }

  it should "not dispatch actions if params not changed when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val props = getSystemUserPanelProps(dispatch)
    val component = <(SystemUserPanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    renderIntoDocument(component)

    Succeeded
  }

  it should "dispatch actions when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemUserActions]
    val prevProps = {
      val systemId = 12
      val props = getSystemUserPanelProps(dispatch, actions = actions, selectedSystemId = Some(systemId))
      props.copy(data = props.data.copy(systemId = Some(systemId)))
    }
    val comp = renderIntoDocument(<(SystemUserPanel())(^.wrapped := prevProps)())
    val containerElement = findReactElement(comp).parentNode
    val newSystemId = 123
    val props = prevProps.copy(
      selectedSystemId = Some(newSystemId)
    )
    val listFetchAction = SystemUserListFetchAction(
      FutureTask("Fetching SystemUsers", Future.successful(SystemUserListResp(Nil, None))), newSystemId, None
    )
    (actions.systemUserListFetch _).expects(dispatch, newSystemId, None, None).returning(listFetchAction)

    //then
    dispatch.expects(listFetchAction)

    //when
    ReactDOM.render(<(SystemUserPanel())(^.wrapped := props)(), containerElement)

    Succeeded
  }

  it should "not dispatch actions if params not changed when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val prevProps = {
      val systemId = 123
      val props = getSystemUserPanelProps(dispatch, selectedSystemId = Some(systemId))
      props.copy(data = props.data.copy(systemId = Some(systemId)))
    }
    val comp = renderIntoDocument(<(SystemUserPanel())(^.wrapped := prevProps)())
    val containerElement = findReactElement(comp).parentNode
    val props = prevProps.copy(
      data = prevProps.data.copy(
        dataList = Nil
      )
    )

    //then
    dispatch.expects(*).never()

    //when
    ReactDOM.render(<(SystemUserPanel())(^.wrapped := props)(), containerElement)

    Succeeded
  }

  it should "render component" in {
    //given
    val systemId = 123
    val props = getSystemUserPanelProps(selectedSystemId = Some(systemId))
    val component = <(SystemUserPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemUserPanel(result, props)
  }

  private def getSystemUserPanelProps(dispatch: Dispatch = mockFunction[Any, Any],
                                      actions: SystemUserActions = mock[SystemUserActions],
                                      data: SystemUserState = SystemUserState(
                                        dataList = List(SystemUserData(
                                          userId = 1,
                                          login = "test_login_1",
                                          lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
                                          updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
                                          createdAt = DateTime("2018-12-03T11:29:01.234Z"),
                                          version = 123
                                        ))
                                      ),
                                      selectedSystemId: Option[Int] = None): SystemUserPanelProps = {

    SystemUserPanelProps(
      dispatch = dispatch,
      actions = actions,
      data = data,
      selectedSystemId = selectedSystemId
    )
  }

  private def assertSystemUserPanel(result: ComponentInstance, props: SystemUserPanelProps): Assertion = {
    assertDOMComponent(result, <.div()(), { case List(tablePanel) =>
      assertComponent(tablePanel, SystemUserTablePanel) {
        case SystemUserTablePanelProps(data, selectedUserId, _, _) =>
          data shouldBe props.data
          selectedUserId shouldBe None
      }
    })
  }
}
