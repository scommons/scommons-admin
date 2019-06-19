package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.company.CompanyListResp
import scommons.admin.client.api.user._
import scommons.admin.client.company.CompanyActions
import scommons.admin.client.company.CompanyActions.CompanyListFetchAction
import scommons.client.ui.select.{SearchSelect, SearchSelectProps, SelectData}
import scommons.client.ui.{PasswordField, PasswordFieldProps, TextField, TextFieldProps}
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils

import scala.concurrent.Future

class UserEditPanelSpec extends TestSpec with ShallowRendererUtils {
  
  it should "call onChange, onEnter when in login field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getUserEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, TextField).head
    val value = "updated"
    val data = props.initialData.copy(
      user = props.initialData.user.copy(
        login = value
      )
    )
    
    //then
    onChange.expects(data)
    onEnter.expects()
    
    //when
    fieldProps.onChange(value)
    fieldProps.onEnter()
  }

  it should "call onChange, onEnter when in password field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getUserEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findComponentProps(comp, PasswordField)
    val password = "updated"
    val data = props.initialData.copy(
      user = props.initialData.user.copy(
        password = password
      )
    )
    
    //then
    onChange.expects(data)
    onEnter.expects()
    
    //when
    fieldProps.onChange(password)
    fieldProps.onEnter()
  }

  it should "call companyListFetch when onLoad in company field" in {
    //given
    val companyActions = mock[CompanyActions]
    val props = getUserEditPanelProps(companyActions = companyActions)
    val comp = shallowRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findComponentProps(comp, SearchSelect)
    val inputValue = "some input"
    val action = CompanyListFetchAction(FutureTask("Fetching",
      Future.successful(CompanyListResp(Nil))), Some(0))

    //then
    (companyActions.companyListFetch _).expects(props.dispatch, Some(0), Some(inputValue))
      .returning(action)

    //when
    fieldProps.onLoad(inputValue)
  }

  it should "call onChange when onChange(Some) in company field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val companyActions = mock[CompanyActions]
    val props = getUserEditPanelProps(companyActions = companyActions, onChange = onChange)
    val comp = shallowRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findComponentProps(comp, SearchSelect)
    val value = SelectData("2", "Comp 2")
    val data = props.initialData.copy(
      user = props.initialData.user.copy(
        company = UserCompanyData(2, "Comp 2")
      )
    )

    //then
    onChange.expects(data)

    //when
    fieldProps.onChange(Some(value))
  }

  it should "call onChange when onChange(None) in company field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val companyActions = mock[CompanyActions]
    val props = getUserEditPanelProps(companyActions = companyActions, onChange = onChange)
    val comp = shallowRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findComponentProps(comp, SearchSelect)
    val data = props.initialData.copy(
      user = props.initialData.user.copy(
        company = UserCompanyData(-1, "")
      )
    )

    //then
    onChange.expects(data)

    //when
    fieldProps.onChange(None)
  }

  it should "call onChange, onEnter when in firstName field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getUserEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, TextField)(1)
    val value = "updated"
    val data = props.initialData.copy(
      profile = props.initialData.profile.copy(
        firstName = value
      )
    )

    //then
    onChange.expects(data)
    onEnter.expects()

    //when
    fieldProps.onChange(value)
    fieldProps.onEnter()
  }

  it should "call onChange, onEnter when in lastName field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getUserEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, TextField)(2)
    val value = "updated"
    val data = props.initialData.copy(
      profile = props.initialData.profile.copy(
        lastName = value
      )
    )

    //then
    onChange.expects(data)
    onEnter.expects()

    //when
    fieldProps.onChange(value)
    fieldProps.onEnter()
  }

  it should "call onChange, onEnter when in email field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getUserEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, TextField)(3)
    val value = "updated"
    val data = props.initialData.copy(
      profile = props.initialData.profile.copy(
        email = value
      )
    )

    //then
    onChange.expects(data)
    onEnter.expects()

    //when
    fieldProps.onChange(value)
    fieldProps.onEnter()
  }

  it should "call onChange, onEnter when in phone field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getUserEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, TextField)(4)
    val value = "updated"
    val data = props.initialData.copy(
      profile = props.initialData.profile.copy(
        phone = Some(value)
      )
    )

    //then
    onChange.expects(data)
    onEnter.expects()

    //when
    fieldProps.onChange(value)
    fieldProps.onEnter()
  }

  it should "render component" in {
    //given
    val props = getUserEditPanelProps()
    val component = <(UserEditPanel())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertUserEditPanel(result, props)
  }

  it should "render component and requestFocus on first input field" in {
    //given
    val props = getUserEditPanelProps(requestFocus = true)
    val component = <(UserEditPanel())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertUserEditPanel(result, props)
  }

  private def getUserEditPanelProps(dispatch: Dispatch = mock[Dispatch],
                                    companyActions: CompanyActions = mock[CompanyActions],
                                    initialData: UserDetailsData = UserDetailsData(
                                      user = UserData(
                                        id = Some(11),
                                        company = UserCompanyData(1, "Test Company"),
                                        login = "test_login",
                                        password = "test_password",
                                        active = true
                                      ),
                                      profile = UserProfileData(
                                        email = "test@email.com",
                                        firstName = "test first",
                                        lastName = "test lastName",
                                        phone = Some("0123456789")
                                      )
                                    ),
                                    requestFocus: Boolean = false,
                                    onChange: UserDetailsData => Unit = _ => (),
                                    onEnter: () => Unit = () => ()): UserEditPanelProps = {

    UserEditPanelProps(
      dispatch = dispatch,
      actions = companyActions,
      initialData = initialData,
      requestFocus = requestFocus,
      onChange = onChange,
      onEnter = onEnter
    )
  }

  private def assertUserEditPanel(result: ShallowInstance, props: UserEditPanelProps): Unit = {
    val data = props.initialData

    assertNativeComponent(result, <.div(^.className := "form-horizontal")(), {
      case List(
      loginComp,
      passwordComp,
      companyComp,
      firstNameComp,
      lastNameComp,
      emailComp,
      phoneComp,
      noteComp
      ) =>
        assertNativeComponent(loginComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("*Login"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField) {
              case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                text shouldBe data.user.login
                requestFocus shouldBe props.requestFocus
                requestSelect shouldBe props.requestFocus
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe false
            }
          })
        })
        assertNativeComponent(passwordComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("*Password"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, PasswordField) {
              case PasswordFieldProps(password, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                password shouldBe data.user.password
                requestFocus shouldBe false
                requestSelect shouldBe false
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe false
            }
          })
        })
        assertNativeComponent(companyComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("*Company"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, SearchSelect) {
              case SearchSelectProps(selected, _, _, isClearable, readOnly) =>
                selected shouldBe {
                  if (data.user.company.id == -1) None
                  else Some(SelectData(data.user.company.id.toString, data.user.company.name))
                }
                isClearable shouldBe false
                readOnly shouldBe false
            }
          })
        })
        assertNativeComponent(firstNameComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("*First Name"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField) {
              case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                text shouldBe data.profile.firstName
                requestFocus shouldBe false
                requestSelect shouldBe false
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe false
            }
          })
        })
        assertNativeComponent(lastNameComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("*Last Name"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField) {
              case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                text shouldBe data.profile.lastName
                requestFocus shouldBe false
                requestSelect shouldBe false
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe false
            }
          })
        })
        assertNativeComponent(emailComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("*E-mail"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField) {
              case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                text shouldBe data.profile.email
                requestFocus shouldBe false
                requestSelect shouldBe false
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe false
            }
          })
        })
        assertNativeComponent(phoneComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("Phone"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField) {
              case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                text shouldBe data.profile.phone.getOrElse("")
                requestFocus shouldBe false
                requestSelect shouldBe false
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe false
            }
          })
        })
        assertNativeComponent(noteComp, <.p()(<.small()("(*) Indicates required fields")))
    })
  }
}
