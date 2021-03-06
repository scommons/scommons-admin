package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.user._
import scommons.admin.client.api.user.system.UserSystemData
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.user.UserActions._
import scommons.admin.client.user.system._
import scommons.client.ui._
import scommons.nodejs.test.AsyncTestSpec
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test._

import scala.concurrent.Future

class UserPanelSpec extends AsyncTestSpec with BaseTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "dispatch actions when select user" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    var selectedParams: Option[UserParams] = None
    val onChangeParams = { params: UserParams =>
      selectedParams = Some(params)
    }
    val respData = mock[UserDetailsData]
    val props = getUserPanelProps(dispatch, userActions = actions, onChangeParams = onChangeParams)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val tablePanelProps = findComponentProps(comp, UserTablePanel)
    val userId = 22
    val params = props.selectedParams.copy(userId = Some(userId))
    val action = UserFetchAction(
      FutureTask("Fetching", Future.successful(UserDetailsResp(respData)))
    )
    (actions.userFetch _).expects(dispatch, userId).returning(action)
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
    val actions = mock[UserActions]
    val onChangeParams = mockFunction[UserParams, Unit]
    val props = getUserPanelProps(dispatch, userActions = actions, onChangeParams = onChangeParams)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val tablePanelProps = findComponentProps(comp, UserTablePanel)
    val params = props.selectedParams.copy(userId = None)
    val offset = Some(10)
    val symbols = Some("test")
    val action = UserListFetchAction(
      FutureTask("Fetching", Future.successful(UserListResp(Nil, None))),
      offset
    )
    (actions.userListFetch _).expects(dispatch, offset, symbols).returning(action)

    //then
    dispatch.expects(action)
    onChangeParams.expects(params)
    
    //when
    tablePanelProps.onLoadData(offset, symbols)
    
    Succeeded
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

    Succeeded
  }

  ignore should "dispatch UserCreateAction when onSave in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val userActions = mock[UserActions]
    val props = {
      val props = getUserPanelProps(dispatch, userActions = userActions)
      props.copy(data = props.data.copy(showCreatePopup = true))
    }
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
    
    Succeeded
  }

  ignore should "dispatch UserCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val props = {
      val props = getUserPanelProps(dispatch)
      props.copy(data = props.data.copy(showCreatePopup = true))
    }
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, UserEditPopup)

    //then
    dispatch.expects(UserCreateRequestAction(create = false))
    
    //when
    createPopupProps.onCancel()

    Succeeded
  }

  it should "dispatch UserUpdateAction when onSave in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val userActions = mock[UserActions]
    val props: UserPanelProps = {
      val props = getUserPanelProps(dispatch, userActions = userActions)
      props.copy(data = props.data.copy(showEditPopup = true))
    }
    val data = props.data.userDetails.get
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val editPopupProps = findComponentProps(comp, UserEditPopup)
    val action = UserUpdateAction(
      FutureTask("Updating", Future.successful(UserDetailsResp(data)))
    )
    (userActions.userUpdate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    editPopupProps.onSave(data)

    Succeeded
  }

  it should "dispatch UserUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val userActions = mock[UserActions]
    val props = {
      val props = getUserPanelProps(dispatch, userActions = userActions)
      props.copy(data = props.data.copy(showEditPopup = true))
    }
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val editPopupProps = findComponentProps(comp, UserEditPopup)

    //then
    dispatch.expects(UserUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()

    Succeeded
  }

  it should "dispatch actions when mount" in {
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
    val renderer = createTestRenderer(<(UserPanel())(^.wrapped := props)())

    //cleanup
    renderer.unmount()
    Succeeded
  }

  it should "not dispatch actions if params not changed when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val onChangeParams = mockFunction[UserParams, Unit]
    val props = getUserPanelProps(dispatch, onChangeParams = onChangeParams)

    //then
    dispatch.expects(*).never()
    onChangeParams.expects(*).never()

    //when
    val renderer = createTestRenderer(<(UserPanel())(^.wrapped := props)())

    //cleanup
    renderer.unmount()
    Succeeded
  }

  it should "dispatch actions when update" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val respData = mock[UserDetailsData]
    val onChangeParams = mockFunction[UserParams, Unit]
    val prevProps = getUserPanelProps(dispatch, userActions = actions, onChangeParams = onChangeParams)
    val renderer = createTestRenderer(<(UserPanel())(^.wrapped := prevProps)())
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
    renderer.update(<(UserPanel())(^.wrapped := props)())

    //cleanup
    renderer.unmount()
    Succeeded
  }

  it should "not dispatch actions if params not changed when update" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val onChangeParams = mockFunction[UserParams, Unit]
    val prevProps = getUserPanelProps(dispatch, onChangeParams = onChangeParams)
    val renderer = createTestRenderer(<(UserPanel())(^.wrapped := prevProps)())
    val props = prevProps.copy()
    props should not be theSameInstanceAs(prevProps)

    //then
    dispatch.expects(*).never()
    onChangeParams.expects(*).never()

    //when
    renderer.update(<(UserPanel())(^.wrapped := props)())

    //cleanup
    renderer.unmount()
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

  it should "render component and show edit popup" in {
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
                                userSystemActions: UserSystemActions = mock[UserSystemActions],
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
                                systemData: UserSystemState = UserSystemState(
                                  systems = List(UserSystemData(1, "admin", isSelected = false)),
                                  userId = Some(11)
                                ),
                                selectedParams: UserParams = UserParams(Some(11)),
                                onChangeParams: UserParams => Unit = _ => ()): UserPanelProps = {

    UserPanelProps(
      dispatch = dispatch,
      companyActions = companyActions,
      userActions = userActions,
      userSystemActions = userSystemActions,
      data = data,
      systemData = systemData,
      selectedParams = selectedParams,
      onChangeParams = onChangeParams
    )
  }

  private def assertUserPanel(result: ShallowInstance, props: UserPanelProps): Assertion = {
    val selectedData = props.data.userDetails

    def assertUserSystemPanel(component: ReactElement, data: UserDetailsData): Assertion = {
      val wrapped = new FunctionComponent[Unit] {
        protected def render(compProps: Props): ReactElement = {
          <.div()(component)
        }
      }
      val result = shallowRender(<(wrapped())()())

      assertNativeComponent(result, <.div()(), { children: List[ShallowInstance] =>
        inside(children) { case List(comp) =>
          assertComponent(comp, UserSystemPanel) {
            case UserSystemPanelProps(dispatch, actions, systemData, selectedUser) =>
              dispatch shouldBe props.dispatch
              actions shouldBe props.userSystemActions
              systemData shouldBe props.systemData
              selectedUser shouldBe (props.selectedParams.tab.getOrElse(UserDetailsTab.apps) match {
                case UserDetailsTab.apps => Some(data.user)
                case _ => None
              })
          }
        }
      })
    }

    def assertComponents(buttonsPanel: ShallowInstance,
                         tablePanel: ShallowInstance,
                         createPopup: Option[ShallowInstance],
                         detailsPanel: Option[ShallowInstance],
                         editPopup: Option[ShallowInstance]): Assertion = {

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
        case UserTablePanelProps(data, selectedUserId, _, _) =>
          data shouldBe props.data
          selectedUserId shouldBe props.selectedParams.userId
      }

      if (props.data.showCreatePopup) {
        createPopup.isDefined shouldBe true
        assertComponent(createPopup.get, UserEditPopup) {
          case UserEditPopupProps(dispatch, actions, title, initialData, _, _) =>
            dispatch shouldBe props.dispatch
            actions shouldBe props.companyActions
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
      }

      detailsPanel.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(detailsPanel.get, UserDetailsPanel) {
          case UserDetailsPanelProps(renderSystems, profile, selectedTab, _) =>
            profile shouldBe data.profile
            selectedTab shouldBe props.selectedParams.tab

            assertUserSystemPanel(renderSystems.apply(null), data)
        }
      }
      
      editPopup.isEmpty shouldBe (selectedData.isEmpty && !props.data.showEditPopup)
      selectedData.foreach { data =>
        assertComponent(editPopup.get, UserEditPopup) {
          case UserEditPopupProps(dispatch, actions, title, initialData, _, _) =>
            dispatch shouldBe props.dispatch
            actions shouldBe props.companyActions
            title shouldBe "Edit User"
            initialData shouldBe data
        }
      }
      Succeeded
    }
    
    assertNativeComponent(result, <.div()(), { children: List[ShallowInstance] =>
      inside(children) {
        case List(bp, tp) =>
          assertComponents(bp, tp, None, None, None)
        case List(bp, tp, createPopup) if props.data.showCreatePopup =>
          assertComponents(bp, tp, Some(createPopup), None, None)
        case List(bp, tp, details, editPopup) if props.data.showEditPopup =>
          assertComponents(bp, tp, None, Some(details), Some(editPopup))
        case List(bp, tp, details) if props.data.showEditPopup =>
          assertComponents(bp, tp, None, Some(details), None)
      }
    })
  }
}
