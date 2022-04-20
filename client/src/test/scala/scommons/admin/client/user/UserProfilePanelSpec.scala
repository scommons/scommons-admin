package scommons.admin.client.user

import scommons.admin.client.api.user._
import scommons.admin.client.user.UserProfilePanel._
import scommons.client.ui.TextFieldProps
import scommons.react.test._

class UserProfilePanelSpec extends TestSpec with TestRendererUtils {

  UserProfilePanel.firstNameComp = mockUiComponent("firstNameComp")
  UserProfilePanel.lastNameComp = mockUiComponent("lastNameComp")
  UserProfilePanel.emailComp = mockUiComponent("emailComp")
  UserProfilePanel.phoneComp = mockUiComponent("phoneComp")
  
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
    assertNativeComponent(result, <.div(^.className := "form-horizontal")(
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("First Name"),
        <.div(^.className := "controls")(
          <(firstNameComp())(^.assertWrapped(inside(_) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe props.data.firstName
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe true
          }))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("Last Name"),
        <.div(^.className := "controls")(
          <(lastNameComp())(^.assertWrapped(inside(_) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe props.data.lastName
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe true
          }))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("E-mail"),
        <.div(^.className := "controls")(
          <(emailComp())(^.assertWrapped(inside(_) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe props.data.email
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe true
          }))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("Phone"),
        <.div(^.className := "controls")(
          <(phoneComp())(^.assertWrapped(inside(_) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe props.data.phone.getOrElse("")
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe true
          }))()
        )
      )
    ))
  }
}
