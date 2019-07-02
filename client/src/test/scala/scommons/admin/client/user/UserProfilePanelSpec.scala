package scommons.admin.client.user

import scommons.admin.client.api.user._
import scommons.client.ui.{TextField, TextFieldProps}
import scommons.react.test.TestSpec
import scommons.react.test.raw.TestInstance
import scommons.react.test.util.TestRendererUtils

class UserProfilePanelSpec extends TestSpec with TestRendererUtils {
  
  it should "render component" in {
    //given
    val props = UserProfilePanelProps(UserProfileData(
      email = "test@email.com",
      firstName = "test first",
      lastName = "test lastName",
      phone = Some("0123456789")
    ))

    //when
    val result = testRender(<(UserProfilePanel())(^.wrapped := props)()).children(0)

    //then
    assertUserProfilePanel(result, props)
  }

  private def assertUserProfilePanel(result: TestInstance, props: UserProfilePanelProps): Unit = {
    assertNativeComponent(result, <.div(^.className := "form-horizontal")(), {
      case List(
      firstNameComp,
      lastNameComp,
      emailComp,
      phoneComp
      ) =>
        assertNativeComponent(firstNameComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("First Name"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertTestComponent(fieldComp, TextField) {
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
        assertNativeComponent(lastNameComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("Last Name"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertTestComponent(fieldComp, TextField) {
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
        assertNativeComponent(emailComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("E-mail"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertTestComponent(fieldComp, TextField) {
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
        assertNativeComponent(phoneComp, <.div(^.className := "control-group")(), { case List(labelComp, controls) =>
          assertNativeComponent(labelComp, <.label(^.className := "control-label")("Phone"))
          assertNativeComponent(controls, <.div(^.className := "controls")(), { case List(fieldComp) =>
            assertTestComponent(fieldComp, TextField) {
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
