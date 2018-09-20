package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.elements.ReactElement
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions._
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui._
import scommons.client.ui.tab._

import scala.concurrent.Future

class UserPanelSpec extends TestSpec {

  it should "dispatch UserCreateAction when onSave in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val state = UserState()
    val props = UserPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, UserEditPopup)
    val data = mock[UserDetailsData]
    val action = UserCreateAction(
      FutureTask("Creating", Future.successful(UserDetailsResp(data)))
    )
    (actions.userCreate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(UserCreateRequestAction(create = false))
    dispatch.expects(action)
    
    //when
    createPopupProps.onSave(data)
  }

  it should "dispatch UserCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val state = UserState()
    val props = UserPanelProps(dispatch, actions, state)
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
    val actions = mock[UserActions]
    val data = mock[UserDetailsData]
    val state = UserState(
      selected = Some(data)
    )
    val props = UserPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, UserEditPopup)(1)
    val action = UserUpdateAction(
      FutureTask("Updating", Future.successful(UserDetailsResp(data)))
    )
    (actions.userUpdate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(UserUpdateRequestAction(update = false))
    dispatch.expects(action)
    
    //when
    editPopupProps.onSave(data)
  }

  it should "dispatch UserUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val data = mock[UserDetailsData]
    val state = UserState(
      selected = Some(data)
    )
    val props = UserPanelProps(dispatch, actions, state)
    val comp = shallowRender(<(UserPanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, UserEditPopup)(1)

    //then
    dispatch.expects(UserUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "render component" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserActions]
    val state = UserState(List(UserData(
      id = Some(11),
      company = UserCompanyData(1, "Test Company"),
      login = "test_login",
      password = "test",
      active = true
    )))
    val props = UserPanelProps(dispatch, actions, state)
    val component = <(UserPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserPanel(result, props)
  }

  it should "render component and show create popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserActions]
    val state = UserState(
      dataList = List(UserData(
        id = Some(11),
        company = UserCompanyData(1, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      )),
      showCreatePopup = true
    )
    val props = UserPanelProps(dispatch, actions, state)
    val component = <(UserPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserPanel(result, props)
  }

  it should "render component and show edit popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserActions]
    val state = UserState(
      dataList = List(UserData(
        id = Some(11),
        company = UserCompanyData(1, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      )),
      selected = Some(UserDetailsData(
        user = UserData(
          id = Some(11),
          company = UserCompanyData(1, "Test Company"),
          login = "updated_login",
          password = "updated_password",
          active = true
        ),
        profile = UserProfileData(
          email = "test@email.com",
          firstName = "Firstname",
          lastName = "Lastname",
          phone = Some("0123 456 789")
        )
      )),
      showEditPopup = true
    )
    val props = UserPanelProps(dispatch, actions, state)
    val component = <(UserPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserPanel(result, props)
  }

  private def assertUserPanel(result: ComponentInstance, props: UserPanelProps): Unit = {
    val selectedData = props.data.selected
    
    def assertUserProfilePanel(component: ReactElement, data: UserProfileData): Assertion = {
      val wrapped = React.createClass[Unit, Unit] { _ =>
        <.div()(component)
      }
      val result = shallowRender(<(wrapped)()())

      assertDOMComponent(result, <.div()(), { case List(comp) =>
        assertComponent(comp, UserProfilePanel(), { cProps: UserProfilePanelProps =>
          inside(cProps) { case UserProfilePanelProps(resultData) =>
            resultData shouldBe data
          }
        })
      })
    }

    def assertComponents(buttonsPanel: ComponentInstance,
                         tablePanel: ComponentInstance,
                         createPopup: ComponentInstance,
                         tabPanel: Option[ComponentInstance],
                         editPopup: Option[ComponentInstance]): Assertion = {

      assertComponent(buttonsPanel, ButtonsPanel(), { bpProps: ButtonsPanelProps =>
        inside(bpProps) { case ButtonsPanelProps(buttons, actions, dispatch, group, _) =>
          buttons shouldBe List(Buttons.ADD, Buttons.EDIT)
          actions.enabledCommands shouldBe {
            Set(Buttons.ADD.command) ++ selectedData.map(_ => Buttons.EDIT.command)
          }
          dispatch shouldBe props.dispatch
          group shouldBe false
        }
      })
      assertComponent(tablePanel, UserTablePanel(), { tpProps: UserTablePanelProps =>
        inside(tpProps) { case UserTablePanelProps(dispatch, actions, data) =>
          dispatch shouldBe props.dispatch
          actions shouldBe props.actions
          data shouldBe props.data
        }
      })

      assertComponent(createPopup, UserEditPopup(), { ppProps: UserEditPopupProps =>
        inside(ppProps) { case UserEditPopupProps(show, title, _, _, initialData) =>
          show shouldBe props.data.showCreatePopup
          title shouldBe "New User"
          initialData shouldBe UserDetailsData(
            user = UserData(
              id = None,
              company = UserCompanyData(1, "Test Company"),
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
      })
      
      tabPanel.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(tabPanel.get, TabPanel(), { ppProps: TabPanelProps =>
          inside(ppProps) { case TabPanelProps(items, selectedIndex, _, direction) =>
            selectedIndex shouldBe 0
            direction shouldBe TabDirection.Top

            items.size shouldBe 1
            inside(items.head) { case TabItemData(title, image, component, render) =>
              title shouldBe "Profile"
              image shouldBe Some(AdminImagesCss.vcard)
              component shouldBe None
              render should not be None

              assertUserProfilePanel(render.get.apply(null), data.profile)
            }
          }
        })
      }
      
      editPopup.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(editPopup.get, UserEditPopup(), { ppProps: UserEditPopupProps =>
          inside(ppProps) { case UserEditPopupProps(show, title, _, _, initialData) =>
            show shouldBe props.data.showEditPopup
            title shouldBe "Edit User"
            initialData shouldBe data
          }
        })
      }
      Succeeded
    }
    
    assertDOMComponent(result, <.div()(), {
      case List(bp, tp, createPopup) =>
        assertComponents(bp, tp, createPopup, None, None)
      case List(bp, tp, createPopup, tab, editPopup) =>
        assertComponents(bp, tp, createPopup, Some(tab), Some(editPopup))
    })
  }
}
