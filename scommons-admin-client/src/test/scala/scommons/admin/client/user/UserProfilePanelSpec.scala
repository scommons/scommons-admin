package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import scommons.admin.client.api.user._
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.{TextField, TextFieldProps}

class UserProfilePanelSpec extends TestSpec {
  
  it should "render component" in {
    //given
    val props = UserProfilePanelProps(UserProfileData(
      email = "test@email.com",
      firstName = "test first",
      lastName = "test lastName",
      phone = Some("0123456789")
    ))
    val component = <(UserProfilePanel())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertUserProfilePanel(result, props)
  }

  private def assertUserProfilePanel(result: ComponentInstance, props: UserProfilePanelProps): Unit = {
    assertDOMComponent(result, <.div(^.className := "form-horizontal")(), {
      case List(
      firstNameComp,
      lastNameComp,
      emailComp,
      phoneComp
      ) =>
        assertDOMComponent(firstNameComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("First Name"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField) {
              case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                text shouldBe props.data.firstName
                requestFocus shouldBe false
                requestSelect shouldBe false
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe true
            }
          })
        })
        assertDOMComponent(lastNameComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("Last Name"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField) {
              case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                text shouldBe props.data.lastName
                requestFocus shouldBe false
                requestSelect shouldBe false
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe true
            }
          })
        })
        assertDOMComponent(emailComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("E-mail"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField) {
              case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                text shouldBe props.data.email
                requestFocus shouldBe false
                requestSelect shouldBe false
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe true
            }
          })
        })
        assertDOMComponent(phoneComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertDOMComponent(labelComp, <.label(^.className := "control-label")("Phone"))
          assertDOMComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertComponent(fieldComp, TextField) {
              case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
                text shouldBe props.data.phone.getOrElse("")
                requestFocus shouldBe false
                requestSelect shouldBe false
                className shouldBe None
                placeholder shouldBe None
                readOnly shouldBe true
            }
          })
        })
    })
  }
}
