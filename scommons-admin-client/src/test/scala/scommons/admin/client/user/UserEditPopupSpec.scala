package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import scommons.admin.client.api.user._
import scommons.client.test.TestSpec

class UserEditPopupSpec extends TestSpec {

  it should "enable save button if all required fields are filled" in {
    //given
    val props = getUserEditPopupProps()
    val data = props.initialData
    
    //when & then
    props.isSaveEnabled(data) shouldBe true
    props.isSaveEnabled(data.copy(user = data.user.copy(login = " "))) shouldBe false
    props.isSaveEnabled(data.copy(user = data.user.copy(password = ""))) shouldBe false
    props.isSaveEnabled(data.copy(user = data.user.copy(password = " "))) shouldBe true
    props.isSaveEnabled(data.copy(profile = data.profile.copy(firstName = " "))) shouldBe false
    props.isSaveEnabled(data.copy(profile = data.profile.copy(lastName = " "))) shouldBe false
    props.isSaveEnabled(data.copy(profile = data.profile.copy(email = " "))) shouldBe false
    props.isSaveEnabled(data.copy(profile = data.profile.copy(phone = Some(" ")))) shouldBe false
    props.isSaveEnabled(data.copy(profile = data.profile.copy(phone = None))) shouldBe true
  }
  
  it should "render edit panel inside popup" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val onSave = mockFunction[Unit]
    val props = getUserEditPopupProps()
    val wrapper = React.createClass[Unit, Unit] { _ =>
      props.render(props.initialData, requestFocus = true, onChange, onSave)
    }
    val component = <(wrapper)()()

    //when
    val result = shallowRender(component)

    //then
    assertComponent(result, UserEditPanel(), { pProps: UserEditPanelProps =>
      inside(pProps) { case UserEditPanelProps(initialData, requestFocus, pOnChange, pOnEnter) =>
        initialData shouldBe props.initialData
        requestFocus shouldBe true
        pOnChange shouldBe onChange
        pOnEnter shouldBe onSave
      }
    })
  }

  private def getUserEditPopupProps(show: Boolean = true,
                                    title: String = "test title",
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
                                    onSave: UserDetailsData => Unit = _ => (),
                                    onCancel: () => Unit = () => ()): UserEditPopupProps = {

    UserEditPopupProps(
      show = show,
      title = title,
      initialData = initialData,
      onSave = onSave,
      onCancel = onCancel
    )
  }
}
