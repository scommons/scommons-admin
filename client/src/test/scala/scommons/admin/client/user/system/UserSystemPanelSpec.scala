package scommons.admin.client.user.system

import io.github.shogowada.scalajs.reactjs.ReactDOM
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.user.system._
import scommons.admin.client.api.user.{UserCompanyData, UserData}
import scommons.admin.client.user.system.UserSystemActions._
import scommons.client.ui.list.{ListBoxData, PickList, PickListProps}
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec
import scommons.react.test.dom.util.TestDOMUtils
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils

import scala.concurrent.Future

class UserSystemPanelSpec extends TestSpec with ShallowRendererUtils with TestDOMUtils {

  it should "dispatch UserSystemAddAction if add item(s) when onSelectChange" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserSystemActions]
    val version = 123
    val userId = 11
    val userData = UserData(
      id = Some(userId),
      company = UserCompanyData(2, "Test Company"),
      login = "test_login",
      password = "test",
      active = true,
      version = Some(version)
    )
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      userData
    )
    val state = UserSystemState(
      systems = respData.systems,
      userId = respData.user.id
    )
    val props = UserSystemPanelProps(dispatch, actions, state, selectedUser = Some(userData))
    val comp = shallowRender(<(UserSystemPanel())(^.wrapped := props)())
    val pickListProps = findComponentProps(comp, PickList)
    val data = UserSystemUpdateReq(Set(1), version)
    val action = UserSystemAddAction(
      FutureTask("Test", Future.successful(UserSystemResp(respData)))
    )
    (actions.userSystemsAdd _).expects(dispatch, userId, data)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    pickListProps.onSelectChange(Set("1"), true)
  }

  it should "dispatch UserSystemRemoveAction if remove item(s) when onSelectChange" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserSystemActions]
    val userId = 11
    val version = 123
    val userData = UserData(
      id = Some(userId),
      company = UserCompanyData(2, "Test Company"),
      login = "test_login",
      password = "test",
      active = true,
      version = Some(version)
    )
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = true)),
      userData
    )
    val state = UserSystemState(
      systems = respData.systems,
      userId = respData.user.id
    )
    val props = UserSystemPanelProps(dispatch, actions, state, selectedUser = Some(userData))
    val comp = shallowRender(<(UserSystemPanel())(^.wrapped := props)())
    val pickListProps = findComponentProps(comp, PickList)
    val data = UserSystemUpdateReq(Set(1), version)
    val action = UserSystemRemoveAction(
      FutureTask("Test", Future.successful(UserSystemResp(respData)))
    )
    (actions.userSystemsRemove _).expects(dispatch, userId, data)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    pickListProps.onSelectChange(Set("1"), false)
  }

  it should "dispatch UserSystemFetchAction when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserSystemActions]
    val userId = 11
    val userData = UserData(
      id = Some(userId),
      company = UserCompanyData(2, "Test Company"),
      login = "test_login",
      password = "test",
      active = true,
      version = Some(123)
    )
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      userData
    )
    val state = UserSystemState()
    val props = UserSystemPanelProps(dispatch, actions, state, selectedUser = Some(userData))
    val component = <(UserSystemPanel())(^.wrapped := props)()
    val action = UserSystemFetchAction(
      FutureTask("Fetching", Future.successful(UserSystemResp(respData)))
    )
    (actions.userSystemsFetch _).expects(dispatch, userId)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    renderIntoDocument(component)
  }

  it should "not dispatch UserSystemFetchAction if same selectedUser id when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserSystemActions]
    val userId = 11
    val userData = UserData(
      id = Some(userId),
      company = UserCompanyData(2, "Test Company"),
      login = "test_login",
      password = "test",
      active = true,
      version = Some(123)
    )
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      userData
    )
    val state = UserSystemState(
      systems = respData.systems,
      userId = respData.user.id
    )
    val props = UserSystemPanelProps(dispatch, actions, state, selectedUser = Some(userData))
    val component = <(UserSystemPanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    renderIntoDocument(component)
  }

  it should "not dispatch UserSystemFetchAction if selectedUser not defined when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserSystemActions]
    val userId = 11
    val userData = UserData(
      id = Some(userId),
      company = UserCompanyData(2, "Test Company"),
      login = "test_login",
      password = "test",
      active = true,
      version = Some(123)
    )
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      userData
    )
    val state = UserSystemState(
      systems = respData.systems,
      userId = respData.user.id
    )
    val props = UserSystemPanelProps(dispatch, actions, state, selectedUser = None)
    val component = <(UserSystemPanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    renderIntoDocument(component)
  }

  it should "dispatch UserSystemFetchAction when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserSystemActions]
    val userId = 22
    val userData = UserData(
      id = Some(11),
      company = UserCompanyData(2, "Test Company"),
      login = "test_login",
      password = "test",
      active = true,
      version = Some(123)
    )
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      userData
    )
    val state = UserSystemState(
      systems = respData.systems,
      userId = respData.user.id
    )
    val prevProps = UserSystemPanelProps(dispatch, actions, state, selectedUser = Some(userData))
    val comp = renderIntoDocument(<(UserSystemPanel())(^.wrapped := prevProps)())
    val props = UserSystemPanelProps(dispatch, actions, state, selectedUser = Some(
      userData.copy(id = Some(userId))
    ))
    val containerElement = findReactElement(comp).parentNode
    props.selectedUser.get.id should not be prevProps.selectedUser.get.id
    
    val action = UserSystemFetchAction(
      FutureTask("Fetching", Future.successful(UserSystemResp(respData)))
    )
    (actions.userSystemsFetch _).expects(dispatch, userId)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    ReactDOM.render(<(UserSystemPanel())(^.wrapped := props)(), containerElement)
  }

  it should "not dispatch UserSystemFetchAction if same selectedUser id when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserSystemActions]
    val userId = 11
    val userData = UserData(
      id = Some(userId),
      company = UserCompanyData(2, "Test Company"),
      login = "test_login",
      password = "test",
      active = true,
      version = Some(123)
    )
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      userData
    )
    val state = UserSystemState(
      systems = respData.systems,
      userId = respData.user.id
    )
    val prevProps = UserSystemPanelProps(dispatch, actions, state, selectedUser = Some(userData))
    val comp = renderIntoDocument(<(UserSystemPanel())(^.wrapped := prevProps)())
    val props = UserSystemPanelProps(dispatch, mock[UserSystemActions], state, selectedUser = Some(
      userData.copy(login = "changed_login")
    ))
    val containerElement = findReactElement(comp).parentNode
    props should not be prevProps
    props.selectedUser.get.id shouldBe prevProps.selectedUser.get.id
    
    //then
    dispatch.expects(*).never()

    //when
    ReactDOM.render(<(UserSystemPanel())(^.wrapped := props)(), containerElement)
  }

  it should "not dispatch UserSystemFetchAction if selectedUser not defined when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserSystemActions]
    val userId = 11
    val userData = UserData(
      id = Some(userId),
      company = UserCompanyData(2, "Test Company"),
      login = "test_login",
      password = "test",
      active = true,
      version = Some(123)
    )
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      userData
    )
    val state = UserSystemState(
      systems = respData.systems,
      userId = respData.user.id
    )
    val prevProps = UserSystemPanelProps(dispatch, actions, state, selectedUser = Some(userData))
    val comp = renderIntoDocument(<(UserSystemPanel())(^.wrapped := prevProps)())
    val props = UserSystemPanelProps(dispatch, mock[UserSystemActions], state, selectedUser = None)
    val containerElement = findReactElement(comp).parentNode
    props should not be prevProps
    prevProps.selectedUser should not be None
    props.selectedUser shouldBe None
    
    //then
    dispatch.expects(*).never()

    //when
    ReactDOM.render(<(UserSystemPanel())(^.wrapped := props)(), containerElement)
  }

  it should "render component" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserSystemActions]
    val userData = UserData(
      id = Some(11),
      company = UserCompanyData(2, "Test Company"),
      login = "test_login",
      password = "test",
      active = true
    )
    val data = UserSystemRespData(
      List(
        UserSystemData(1, "test_app", isSelected = false),
        UserSystemData(2, "test_app2", isSelected = true),
        UserSystemData(3, "test_app3", isSelected = false)
      ),
      userData
    )
    val state = UserSystemState(
      systems = data.systems,
      userId = data.user.id
    )
    val props = UserSystemPanelProps(dispatch, actions, state, selectedUser = Some(userData))
    val component = <(UserSystemPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserSystemPanel(result, props)
  }

  private def assertUserSystemPanel(result: ShallowInstance, props: UserSystemPanelProps): Unit = {
    assertComponent(result, PickList) {
      case PickListProps(items, selectedIds, preSelectedIds, _, sourceTitle, destTitle) =>
        items shouldBe props.systemData.systems.map { s =>
          ListBoxData(s.id.toString, s.name, Some(AdminImagesCss.computer))
        }
        selectedIds shouldBe props.systemData.systems.filter(_.isSelected).map(_.id.toString).toSet
        preSelectedIds shouldBe Set.empty[String]
        sourceTitle shouldBe "Available apps"
        destTitle shouldBe "Assigned apps"
    }
  }
}
