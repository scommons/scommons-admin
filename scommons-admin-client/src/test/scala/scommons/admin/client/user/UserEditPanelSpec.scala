package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import scommons.admin.client.api.user._
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.{PasswordField, PasswordFieldProps, TextField, TextFieldProps}

class UserEditPanelSpec extends TestSpec {
  
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

  private def getUserEditPanelProps(initialData: UserDetailsData = UserDetailsData(
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
      initialData = initialData,
      requestFocus = requestFocus,
      onChange = onChange,
      onEnter = onEnter
    )
  }

  private def assertUserEditPanel(result: ComponentInstance, props: UserEditPanelProps): Unit = {
    val data = props.initialData

    assertDOMComponent(result, <.div(^.className := "form-horizontal")(), {
      case List(
      loginComp,
      passwordComp,
      firstNameComp,
      lastNameComp,
      emailComp,
      phoneComp,
      companyComp,
      noteComp
      ) =>
        assertDOMComponent(loginComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("*Login"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField(), { fieldProps: TextFieldProps =>
              inside(fieldProps) {
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
        })
        assertDOMComponent(passwordComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("*Password"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, PasswordField(), { fieldProps: PasswordFieldProps =>
              inside(fieldProps) {
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
        })
        assertDOMComponent(firstNameComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("*First Name"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField(), { fieldProps: TextFieldProps =>
              inside(fieldProps) {
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
        })
        assertDOMComponent(lastNameComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("*Last Name"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField(), { fieldProps: TextFieldProps =>
              inside(fieldProps) {
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
        })
        assertDOMComponent(emailComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("*E-mail"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField(), { fieldProps: TextFieldProps =>
              inside(fieldProps) {
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
        })
        assertDOMComponent(phoneComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("Phone"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField(), { fieldProps: TextFieldProps =>
              inside(fieldProps) {
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
        })
        assertDOMComponent(companyComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("*Company"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField(), { fieldProps: TextFieldProps =>
              inside(fieldProps) {
                case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                  text shouldBe data.user.company.name
                  requestFocus shouldBe false
                  requestSelect shouldBe false
                  className shouldBe None
                  placeholder shouldBe None
                  readOnly shouldBe true
              }
            })
          })
        })
        assertDOMComponent(noteComp, <.p()(<.small()("(*) Indicates required fields")))
    })
  }
}
