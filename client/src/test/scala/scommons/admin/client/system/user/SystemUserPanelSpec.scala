package scommons.admin.client.system.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.elements.ReactElement
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.joda.time.DateTime
import org.scalatest._
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.system.user._
import scommons.admin.client.system.user.SystemUserActions._
import scommons.client.ui.tab._
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test.dom.AsyncTestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.{ShallowRendererUtils, TestRendererUtils}

import scala.concurrent.Future

class SystemUserPanelSpec extends AsyncTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

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

  it should "dispatch actions if diff params when mount" in {
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
    val renderer = createTestRenderer(<(SystemUserPanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()

    Succeeded
  }

  it should "not dispatch actions if same params when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val onChangeParams = mockFunction[SystemUserParams, Unit]
    val props = getSystemUserPanelProps(dispatch, onChangeParams = onChangeParams)

    //then
    dispatch.expects(*).never()
    onChangeParams.expects(*).never()

    //when
    val renderer = createTestRenderer(<(SystemUserPanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()

    Succeeded
  }

  it should "dispatch actions if diff params when update" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[SystemUserActions]
    val onChangeParams = mockFunction[SystemUserParams, Unit]
    val prevProps = getSystemUserPanelProps(dispatch, actions = actions, onChangeParams = onChangeParams)
    val renderer = createTestRenderer(<(SystemUserPanel())(^.wrapped := prevProps)())
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
    renderer.update(<(SystemUserPanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()

    Succeeded
  }

  it should "not dispatch actions if same params when update" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val onChangeParams = mockFunction[SystemUserParams, Unit]
    val prevProps = getSystemUserPanelProps(dispatch, onChangeParams = onChangeParams)
    val renderer = createTestRenderer(<(SystemUserPanel())(^.wrapped := prevProps)())
    val props = prevProps.copy(
      data = prevProps.data.copy(
        dataList = Nil
      )
    )

    //then
    dispatch.expects(*).never()
    onChangeParams.expects(*).never()

    //when
    renderer.update(<(SystemUserPanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()

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
      val su = props.data.dataList.head
      props.copy(
        data = props.data.copy(
          params = props.data.params.copy(userId = Some(su.userId)),
          selectedUser = Some(su)
        ),
        selectedParams = props.selectedParams.copy(userId = Some(su.userId))
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

  private def assertSystemUserPanel(result: ShallowInstance, props: SystemUserPanelProps): Assertion = {
    val systemId = props.selectedParams.systemId.get

    def assertSystemUserRolePanel(component: ReactElement): Assertion = {
      val wrapped = React.createClass[Unit, Unit] { _ =>
        <.div()(component)
      }
      val result = shallowRender(<(wrapped)()())

      assertNativeComponent(result, <.div()(), { children: List[ShallowInstance] =>
        val List(comp) = children
        assertComponent(comp, SystemUserRolePanel) {
          case SystemUserRolePanelProps(dispatch, actions, data, resSystemId) =>
            dispatch shouldBe props.dispatch
            actions shouldBe props.actions
            data shouldBe props.data
            resSystemId shouldBe systemId
        }
      })
    }

    def assertComponents(tablePanel: ShallowInstance,
                         rolePanel: Option[ShallowInstance]): Assertion = {
      
      assertComponent(tablePanel, SystemUserTablePanel) {
        case SystemUserTablePanelProps(data, selectedUserId, _, _) =>
          data shouldBe props.data
          selectedUserId shouldBe props.selectedParams.userId
      }
      
      rolePanel.size shouldBe props.data.selectedUser.size
      props.data.selectedUser.foreach { _ =>
        assertComponent(rolePanel.get, TabPanel) {
          case TabPanelProps(items, selectedIndex, _, direction) =>
            items.size shouldBe 1
            selectedIndex shouldBe 0
            direction shouldBe TabDirection.Top
            
            inside(items.head) {
              case TabItemData(title, image, component, render) =>
                title shouldBe "Permissions"
                image shouldBe Some(AdminImagesCss.key)
                component shouldBe None
                render should not be None
                
                assertSystemUserRolePanel(render.get.apply(null))
            }
        }
      }
      Succeeded
    }
    
    assertNativeComponent(result, <.>()(), { children: List[ShallowInstance] =>
      children match {
        case List(tb) => assertComponents(tb, None)
        case List(tb, rolePanel) => assertComponents(tb, Some(rolePanel))
      }
    })
  }
}
