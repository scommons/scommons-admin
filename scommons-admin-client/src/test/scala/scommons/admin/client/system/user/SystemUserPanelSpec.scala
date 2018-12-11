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

  it should "dispatch actions when select user" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemUserActions]
    var selectedParams: Option[SystemUserParams] = None
    val onChangeParams = { params: SystemUserParams =>
      selectedParams = Some(params)
    }
    val props = getSystemUserPanelProps(dispatch, actions = actions, onChangeParams = onChangeParams)
    val systemId = props.selectedParams.systemId.get
    val comp = shallowRender(<(SystemUserPanel())(^.wrapped := props)())
    val tablePanelProps = findComponentProps(comp, SystemUserTablePanel)
    val userId = 22
    val params = props.selectedParams.copy(userId = Some(userId))
    val respData = mock[SystemUserRoleRespData]
    val action = SystemUserRoleFetchAction(
      FutureTask("Fetching Roles", Future.successful(SystemUserRoleResp(respData)))
    )
    (actions.systemUserRolesFetch _).expects(dispatch, systemId, userId).returning(action)
    dispatch.expects(action)

    //when
    tablePanelProps.onChangeSelect(userId)

    //then
    eventually {
      selectedParams shouldBe Some(params)
    }
  }

  it should "dispatch actions when load data" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemUserActions]
    val onChangeParams = mockFunction[SystemUserParams, Unit]
    val props = getSystemUserPanelProps(dispatch, actions = actions, onChangeParams = onChangeParams)
    val systemId = props.selectedParams.systemId.get
    val comp = shallowRender(<(SystemUserPanel())(^.wrapped := props)())
    val tablePanelProps = findComponentProps(comp, SystemUserTablePanel)
    val params = props.selectedParams.copy(userId = None)
    val offset = Some(10)
    val symbols = Some("test")
    val action = SystemUserListFetchAction(
      FutureTask("Fetching SystemUsers", Future.successful(SystemUserListResp(Nil, None))), offset
    )
    (actions.systemUserListFetch _).expects(dispatch, systemId, offset, symbols).returning(action)

    //then
    dispatch.expects(action)
    onChangeParams.expects(params)
    
    //when
    tablePanelProps.onLoadData(offset, symbols)
    
    Succeeded
  }

  it should "dispatch actions when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemUserActions]
    val onChangeParams = mockFunction[SystemUserParams, Unit]
    val systemId = 123
    val userId = 12345
    val props = {
      val props = getSystemUserPanelProps(dispatch, actions = actions, onChangeParams = onChangeParams)
      props.copy(selectedParams = props.selectedParams.copy(systemId = Some(systemId), userId = Some(userId)))
    }
    val component = <(SystemUserPanel())(^.wrapped := props)()
    val listFetchAction = SystemUserListFetchAction(
      FutureTask("Fetching SystemUsers", Future.successful(SystemUserListResp(Nil, None))), None
    )
    val respData = mock[SystemUserRoleRespData]
    val rolesFetchAction = SystemUserRoleFetchAction(
      FutureTask("Fetching Roles", Future.successful(SystemUserRoleResp(respData)))
    )
    (actions.systemUserListFetch _).expects(dispatch, systemId, None, None).returning(listFetchAction)
    (actions.systemUserRolesFetch _).expects(dispatch, systemId, userId).returning(rolesFetchAction)

    //then
    dispatch.expects(listFetchAction)
    dispatch.expects(rolesFetchAction)
    onChangeParams.expects(props.selectedParams)

    //when
    renderIntoDocument(component)

    Succeeded
  }

  it should "not dispatch actions if params not changed when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val onChangeParams = mockFunction[SystemUserParams, Unit]
    val props = getSystemUserPanelProps(dispatch, onChangeParams = onChangeParams)
    val component = <(SystemUserPanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()
    onChangeParams.expects(*).never()

    //when
    renderIntoDocument(component)

    Succeeded
  }

  it should "dispatch actions when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemUserActions]
    val onChangeParams = mockFunction[SystemUserParams, Unit]
    val prevProps = getSystemUserPanelProps(dispatch, actions = actions, onChangeParams = onChangeParams)
    val comp = renderIntoDocument(<(SystemUserPanel())(^.wrapped := prevProps)())
    val containerElement = findReactElement(comp).parentNode
    val newSystemId = 123
    val newUserId = 12345
    val props = prevProps.copy(
      selectedParams = prevProps.selectedParams.copy(systemId = Some(newSystemId), userId = Some(newUserId))
    )
    val listFetchAction = SystemUserListFetchAction(
      FutureTask("Fetching SystemUsers", Future.successful(SystemUserListResp(Nil, None))), None
    )
    val respData = mock[SystemUserRoleRespData]
    val rolesFetchAction = SystemUserRoleFetchAction(
      FutureTask("Fetching Roles", Future.successful(SystemUserRoleResp(respData)))
    )
    (actions.systemUserListFetch _).expects(dispatch, newSystemId, None, None).returning(listFetchAction)
    (actions.systemUserRolesFetch _).expects(dispatch, newSystemId, newUserId).returning(rolesFetchAction)

    //then
    dispatch.expects(listFetchAction)
    dispatch.expects(rolesFetchAction)
    onChangeParams.expects(props.selectedParams)

    //when
    ReactDOM.render(<(SystemUserPanel())(^.wrapped := props)(), containerElement)

    Succeeded
  }

  it should "not dispatch actions if params not changed when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val onChangeParams = mockFunction[SystemUserParams, Unit]
    val prevProps = getSystemUserPanelProps(dispatch, onChangeParams = onChangeParams)
    val comp = renderIntoDocument(<(SystemUserPanel())(^.wrapped := prevProps)())
    val containerElement = findReactElement(comp).parentNode
    val props = prevProps.copy(
      data = prevProps.data.copy(
        dataList = Nil
      )
    )

    //then
    dispatch.expects(*).never()
    onChangeParams.expects(*).never()

    //when
    ReactDOM.render(<(SystemUserPanel())(^.wrapped := props)(), containerElement)

    Succeeded
  }

  it should "render component" in {
    //given
    val props = getSystemUserPanelProps()
    val component = <(SystemUserPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemUserPanel(result, props)
  }

  it should "render component with selected user" in {
    //given
    val props = {
      val props = getSystemUserPanelProps()
      val userId = props.data.dataList.head.userId
      props.copy(
        data = props.data.copy(params = props.data.params.copy(userId = Some(userId))),
        selectedParams = props.selectedParams.copy(userId = Some(userId))
      )
    }
    val component = <(SystemUserPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertSystemUserPanel(result, props)
  }

  private def getSystemUserPanelProps(dispatch: Dispatch = mockFunction[Any, Any],
                                      actions: SystemUserActions = mock[SystemUserActions],
                                      data: SystemUserState = SystemUserState(
                                        params = SystemUserParams(Some(11), Some(12)),
                                        dataList = List(SystemUserData(
                                          userId = 1,
                                          login = "test_login_1",
                                          lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
                                          updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
                                          createdAt = DateTime("2018-12-03T11:29:01.234Z"),
                                          version = 123
                                        ))
                                      ),
                                      selectedParams: SystemUserParams = SystemUserParams(Some(11), Some(12)),
                                      onChangeParams: SystemUserParams => Unit = _ => ()): SystemUserPanelProps = {

    SystemUserPanelProps(
      dispatch = dispatch,
      actions = actions,
      data = data,
      selectedParams = selectedParams,
      onChangeParams = onChangeParams
    )
  }

  private def assertSystemUserPanel(result: ComponentInstance, props: SystemUserPanelProps): Assertion = {
    assertDOMComponent(result, <.div()(), { case List(tablePanel) =>
      assertComponent(tablePanel, SystemUserTablePanel) {
        case SystemUserTablePanelProps(data, selectedUserId, _, _) =>
          data shouldBe props.data
          selectedUserId shouldBe props.selectedParams.userId
      }
    })
  }
}
