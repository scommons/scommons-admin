package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.api.user._
import scommons.admin.client.company.CompanyActions
import scommons.react._
import scommons.react.test.TestSpec
import scommons.react.test.util.ShallowRendererUtils

class UserEditPopupSpec extends TestSpec with ShallowRendererUtils {

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
    val wrapper = new FunctionComponent[Unit] {
      protected def render(compProps: Props): ReactElement = {
        props.render(props.initialData, requestFocus = true, onChange, onSave)
      }
    }
    val component = <(wrapper())()()

    //when
    val result = shallowRender(component)

    //then
    assertComponent(result, UserEditPanel) {
      case UserEditPanelProps(disp, act, initialData, requestFocus, pOnChange, pOnEnter) =>
        disp shouldBe props.dispatch
        act shouldBe props.actions
        initialData shouldBe props.initialData
        requestFocus shouldBe true
        pOnChange shouldBe onChange
        pOnEnter shouldBe onSave
    }
  }

  private def getUserEditPopupProps(dispatch: Dispatch = mock[Dispatch],
                                    companyActions: CompanyActions = mock[CompanyActions],
                                    show: Boolean = true,
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
      dispatch = dispatch,
      actions = companyActions,
      show = show,
      title = title,
      initialData = initialData,
      onSave = onSave,
      onCancel = onCancel
    )
  }
}
