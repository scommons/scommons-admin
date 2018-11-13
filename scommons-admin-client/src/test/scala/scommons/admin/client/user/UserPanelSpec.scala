package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.ReactDOM
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.user._
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions._
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.test.util.TestDOMUtils.findReactElement
import scommons.client.ui._

import scala.concurrent.Future

class UserPanelSpec extends TestSpec {

  it should "call onChangeParams when select user" in {
    //given
    val onChangeParams = mockFunction[UserParams, Unit]
    val props = getUserPanelProps(onChangeParams = onChangeParams)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val tablePanelProps = findComponentProps(comp, UserTablePanel)
    val params = UserParams(Some(22))

    //then
    onChangeParams.expects(params)

    //when
    tablePanelProps.onChangeSelect(params.userId)
  }

  it should "call onChangeParams when select tab" in {
    //given
    val onChangeParams = mockFunction[UserParams, Unit]
    val props = getUserPanelProps(onChangeParams = onChangeParams)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val detailsPanelProps = findComponentProps(comp, UserDetailsPanel)
    val params = props.selectedParams.copy(tab = Some(UserDetailsTab.profile))

    //then
    onChangeParams.expects(params)

    //when
    detailsPanelProps.onChangeTab(params.tab)
  }

  it should "dispatch UserCreateAction when onSave in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val userActions = mock[UserActions]
    val props = getUserPanelProps(dispatch, userActions = userActions)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, UserEditPopup)
    val data = mock[UserDetailsData]
    val action = UserCreateAction(
      FutureTask("Creating", Future.successful(UserDetailsResp(data)))
    )
    (userActions.userCreate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    createPopupProps.onSave(data)
  }

  it should "dispatch UserCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val props = getUserPanelProps(dispatch)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, UserEditPopup)

    //then
    dispatch.expects(UserCreateRequestAction(create = false))
    
    //when
    createPopupProps.onCancel()
  }

  it should "dispatch UserUpdateAction when onSave in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val userActions = mock[UserActions]
    val props: UserPanelProps = getUserPanelProps(dispatch, userActions = userActions)
    val data = props.data.userDetails.get
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, UserEditPopup)(1)
    val action = UserUpdateAction(
      FutureTask("Updating", Future.successful(UserDetailsResp(data)))
    )
    (userActions.userUpdate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    editPopupProps.onSave(data)
  }

  it should "dispatch UserUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val userActions = mock[UserActions]
    val props = getUserPanelProps(dispatch, userActions = userActions)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, UserEditPopup)(1)

    //then
    dispatch.expects(UserUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "dispatch actions when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val respData = mock[UserDetailsData]
    val onChangeParams = mockFunction[UserParams, Unit]
    val userId = 123
    val selectedParams = UserParams(Some(userId))
    val props = {
      val props = getUserPanelProps(dispatch, userActions = actions, onChangeParams = onChangeParams,
        selectedParams = selectedParams)
      props.copy(data = props.data.copy(dataList = Nil))
    }
    val component = <(UserPanel())(^.wrapped := props)()
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
    onChangeParams.expects(selectedParams)

    //when
    renderIntoDocument(component)

    Succeeded
  }

  it should "not dispatch actions if params not changed when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val onChangeParams = mockFunction[UserParams, Unit]
    val props = getUserPanelProps(dispatch, onChangeParams = onChangeParams)
    val component = <(UserPanel())(^.wrapped := props)()

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
    val actions = mock[UserActions]
    val respData = mock[UserDetailsData]
    val onChangeParams = mockFunction[UserParams, Unit]
    val prevProps = getUserPanelProps(dispatch, userActions = actions, onChangeParams = onChangeParams)
    val comp = renderIntoDocument(<(UserPanel())(^.wrapped := prevProps)())
    val containerElement = findReactElement(comp).parentNode
    val newUserId = 123
    val selectedParams = UserParams(Some(newUserId))
    val props = prevProps.copy(
      selectedParams = selectedParams
    )
    val fetchAction = UserFetchAction(
      FutureTask("Fetching User", Future.successful(UserDetailsResp(respData)))
    )
    (actions.userFetch _).expects(dispatch, newUserId).returning(fetchAction)

    //then
    dispatch.expects(fetchAction)
    onChangeParams.expects(selectedParams)

    //when
    ReactDOM.render(<(UserPanel())(^.wrapped := props)(), containerElement)

    Succeeded
  }

  it should "not dispatch actions if params not changed when componentDidUpdate" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val onChangeParams = mockFunction[UserParams, Unit]
    val prevProps = getUserPanelProps(dispatch, onChangeParams = onChangeParams)
    val comp = renderIntoDocument(<(UserPanel())(^.wrapped := prevProps)())
    val containerElement = findReactElement(comp).parentNode
    val props = prevProps.copy(
      data = prevProps.data.copy(
        showCreatePopup = true
      )
    )

    //then
    dispatch.expects(*).never()
    onChangeParams.expects(*).never()

    //when
    ReactDOM.render(<(UserPanel())(^.wrapped := props)(), containerElement)

    Succeeded
  }

  it should "render component" in {
    //given
    val props = {
      val props = getUserPanelProps()
      props.copy(
        data = props.data.copy(
          userDetails = None
        ),
        selectedParams = UserParams()
      )
    }
    val component = <(UserPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserPanel(result, props)
  }

  it should "render component and show create popup" in {
    //given
    val props = {
      val props = getUserPanelProps()
      props.copy(
        data = props.data.copy(
          userDetails = None,
          showCreatePopup = true
        ),
        selectedParams = UserParams()
      )
    }
    val component = <(UserPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserPanel(result, props)
  }

  it should "render component with selected user and show edit popup" in {
    //given
    val props = {
      val props = getUserPanelProps()
      props.copy(
        data = props.data.copy(
          showEditPopup = true
        )
      )
    }
    val component = <(UserPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserPanel(result, props)
  }
  
  private def getUserPanelProps(dispatch: Dispatch = mockFunction[Any, Any],
                                companyActions: CompanyActions = mock[CompanyActions],
                                userActions: UserActions = mock[UserActions],
                                data: UserState = UserState(
                                  params = UserParams(Some(11)),
                                  dataList = List(UserData(
                                    id = Some(11),
                                    company = UserCompanyData(2, "Test Company"),
                                    login = "test_login",
                                    password = "test",
                                    active = true
                                  )),
                                  userDetails = Some(UserDetailsData(
                                    user = UserData(
                                      id = Some(11),
                                      company = UserCompanyData(2, "Test Company"),
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
                                ),
                                selectedParams: UserParams = UserParams(Some(11)),
                                onChangeParams: UserParams => Unit = _ => ()): UserPanelProps = {

    UserPanelProps(
      dispatch = dispatch,
      companyActions = companyActions,
      userActions = userActions,
      data = data,
      selectedParams = selectedParams,
      onChangeParams = onChangeParams
    )
  }

  private def assertUserPanel(result: ComponentInstance, props: UserPanelProps): Unit = {
    val selectedData = props.data.userDetails
    
    def assertComponents(buttonsPanel: ComponentInstance,
                         tablePanel: ComponentInstance,
                         createPopup: ComponentInstance,
                         detailsPanel: Option[ComponentInstance],
                         editPopup: Option[ComponentInstance]): Assertion = {

      assertComponent(buttonsPanel, ButtonsPanel) {
        case ButtonsPanelProps(buttons, actions, dispatch, group, _) =>
          buttons shouldBe List(Buttons.ADD, Buttons.EDIT)
          actions.enabledCommands shouldBe {
            Set(Buttons.ADD.command) ++ selectedData.map(_ => Buttons.EDIT.command)
          }
          dispatch shouldBe props.dispatch
          group shouldBe false
      }
      assertComponent(tablePanel, UserTablePanel) {
        case UserTablePanelProps(dispatch, actions, data, selectedUserId, _) =>
          dispatch shouldBe props.dispatch
          actions shouldBe props.userActions
          data shouldBe props.data
          selectedUserId shouldBe props.selectedParams.userId
      }

      assertComponent(createPopup, UserEditPopup) {
        case UserEditPopupProps(dispatch, actions, show, title, initialData, _, _) =>
          dispatch shouldBe props.dispatch
          actions shouldBe props.companyActions
          show shouldBe props.data.showCreatePopup
          title shouldBe "New User"
          initialData shouldBe UserDetailsData(
            user = UserData(
              id = None,
              company = UserCompanyData(-1, ""),
              login = "",
              password = "",
              active = true
            ),
            profile = UserProfileData(
              email = "",
              firstName = "",
              lastName = "",
              phone = None
            )
          )
      }

      detailsPanel.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(detailsPanel.get, UserDetailsPanel) {
          case UserDetailsPanelProps(profile, selectedTab, _) =>
            profile shouldBe data.profile
            selectedTab shouldBe props.selectedParams.tab
        }
      }
      
      editPopup.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(editPopup.get, UserEditPopup) {
          case UserEditPopupProps(dispatch, actions, show, title, initialData, _, _) =>
            dispatch shouldBe props.dispatch
            actions shouldBe props.companyActions
            show shouldBe props.data.showEditPopup
            title shouldBe "Edit User"
            initialData shouldBe data
        }
      }
      Succeeded
    }
    
    assertDOMComponent(result, <.div()(), {
      case List(bp, tp, createPopup) =>
        assertComponents(bp, tp, createPopup, None, None)
      case List(bp, tp, createPopup, details, editPopup) =>
        assertComponents(bp, tp, createPopup, Some(details), Some(editPopup))
    })
  }
}
