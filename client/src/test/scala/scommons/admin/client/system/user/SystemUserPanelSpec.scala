package scommons.admin.client.system.user

import org.joda.time.DateTime
import org.scalatest._
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.system.user._
import scommons.admin.client.system.user.SystemUserActions._
import scommons.admin.client.system.user.SystemUserPanel._
import scommons.client.ui.tab._
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.ReactElement
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test._

import scala.concurrent.Future

class SystemUserPanelSpec extends AsyncTestSpec with BaseTestSpec with TestRendererUtils {

  SystemUserPanel.systemUserTablePanel = mockUiComponent("SystemUserTablePanel")
  SystemUserPanel.tabPanelComp = mockUiComponent("TabPanel")
  SystemUserPanel.systemUserRolePanel = mockUiComponent("SystemUserRolePanel")

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
    val comp = createTestRenderer(<(SystemUserPanel())(^.wrapped := props)()).root
    val tablePanelProps = findComponentProps(comp, systemUserTablePanel)
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
    val comp = createTestRenderer(<(SystemUserPanel())(^.wrapped := props)()).root
    val tablePanelProps = findComponentProps(comp, systemUserTablePanel)
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
    val result = createTestRenderer(component).root
    
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
    val result = createTestRenderer(component).root
    
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

  private def assertSystemUserPanel(result: TestInstance, props: SystemUserPanelProps): Assertion = {
    val systemId = props.selectedParams.systemId.get

    def assertSystemUserRolePanel(component: ReactElement): Assertion = {
      assertTestComponent(createTestRenderer(component).root, systemUserRolePanel) {
        case SystemUserRolePanelProps(dispatch, actions, data, resSystemId) =>
          dispatch shouldBe props.dispatch
          actions shouldBe props.actions
          data shouldBe props.data
          resSystemId shouldBe systemId
      }
    }

    def assertComponents(tablePanel: TestInstance,
                         rolePanel: Option[TestInstance]): Assertion = {
      
      assertTestComponent(tablePanel, systemUserTablePanel) {
        case SystemUserTablePanelProps(data, selectedUserId, _, _) =>
          data shouldBe props.data
          selectedUserId shouldBe props.selectedParams.userId
      }
      
      rolePanel.size shouldBe props.data.selectedUser.size
      props.data.selectedUser.foreach { _ =>
        assertTestComponent(rolePanel.get, tabPanelComp) {
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
    
    inside(result.children.toList) {
      case List(tb) => assertComponents(tb, None)
      case List(tb, rolePanel) => assertComponents(tb, Some(rolePanel))
    }
  }
}
