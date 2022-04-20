package scommons.admin.client.user

import scommons.admin.client.api.company.CompanyListResp
import scommons.admin.client.api.user._
import scommons.admin.client.company.CompanyActions.CompanyListFetchAction
import scommons.admin.client.company.{CompanyActions, MockCompanyActions}
import scommons.admin.client.user.UserEditPanel._
import scommons.client.ui.select.{SearchSelectProps, SelectData}
import scommons.client.ui.{PasswordFieldProps, TextFieldProps}
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test._

import scala.concurrent.Future

class UserEditPanelSpec extends TestSpec with TestRendererUtils {

  UserEditPanel.textFieldComp = mockUiComponent("TextField")
  UserEditPanel.passwordFieldComp = mockUiComponent("PasswordField")
  UserEditPanel.searchSelectComp = mockUiComponent("SearchSelect")
  
  //noinspection TypeAnnotation
  class Actions {
    val companyListFetch = mockFunction[Dispatch, Option[Int], Option[String], CompanyListFetchAction]

    val actions = new MockCompanyActions(
      companyListFetchMock = companyListFetch
    )
  }

  it should "call onChange, onEnter when in login field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getUserEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = testRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, textFieldComp).head
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
    val comp = testRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findComponentProps(comp, passwordFieldComp)
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
    val companyActions = new Actions
    val props = getUserEditPanelProps(companyActions = companyActions.actions)
    val comp = testRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findComponentProps(comp, searchSelectComp)
    val inputValue = "some input"
    val action = CompanyListFetchAction(FutureTask("Fetching",
      Future.successful(CompanyListResp(Nil))), Some(0))

    //then
    companyActions.companyListFetch.expects(props.dispatch, Some(0), Some(inputValue))
      .returning(action)

    //when
    fieldProps.onLoad(inputValue)
  }

  it should "call onChange when onChange(Some) in company field" in {
    //given
    val onChange = mockFunction[UserDetailsData, Unit]
    val companyActions = mock[CompanyActions]
    val props = getUserEditPanelProps(companyActions = companyActions, onChange = onChange)
    val comp = testRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findComponentProps(comp, searchSelectComp)
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
    val comp = testRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findComponentProps(comp, searchSelectComp)
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
    val comp = testRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, textFieldComp)(1)
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
    val comp = testRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, textFieldComp)(2)
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
    val comp = testRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, textFieldComp)(3)
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
    val comp = testRender(<(UserEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, textFieldComp)(4)
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
    val result = testRender(component)

    //then
    assertUserEditPanel(result, props)
  }

  it should "render component and requestFocus on first input field" in {
    //given
    val props = getUserEditPanelProps(requestFocus = true)
    val component = <(UserEditPanel())(^.wrapped := props)()

    //when
    val result = testRender(component)

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

  private def assertUserEditPanel(result: TestInstance, props: UserEditPanelProps): Unit = {
    val data = props.initialData

    assertNativeComponent(result, <.div(^.className := "form-horizontal")(
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Login"),
        <.div(^.className := "controls")(
          <(textFieldComp())(^.assertWrapped(inside(_) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe data.user.login
              requestFocus shouldBe props.requestFocus
              requestSelect shouldBe props.requestFocus
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe false
          }))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Password"),
        <.div(^.className := "controls")(
          <(passwordFieldComp())(^.assertWrapped(inside(_) {
            case PasswordFieldProps(password, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              password shouldBe data.user.password
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe false
          }))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Company"),
        <.div(^.className := "controls")(
          <(searchSelectComp())(^.assertWrapped(inside(_) {
            case SearchSelectProps(selected, _, _, isClearable, readOnly) =>
              selected shouldBe {
                if (data.user.company.id == -1) None
                else Some(SelectData(data.user.company.id.toString, data.user.company.name))
              }
              isClearable shouldBe false
              readOnly shouldBe false
          }))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*First Name"),
        <.div(^.className := "controls")(
          <(textFieldComp())(^.assertWrapped(inside(_) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe data.profile.firstName
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe false
          }))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Last Name"),
        <.div(^.className := "controls")(
          <(textFieldComp())(^.assertWrapped(inside(_) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe data.profile.lastName
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe false
          }))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*E-mail"),
        <.div(^.className := "controls")(
          <(textFieldComp())(^.assertWrapped(inside(_) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe data.profile.email
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe false
          }))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("Phone"),
        <.div(^.className := "controls")(
          <(textFieldComp())(^.assertWrapped(inside(_) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe data.profile.phone.getOrElse("")
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe None
              placeholder shouldBe None
              readOnly shouldBe false
          }))()
        )
      ),
      <.p()(<.small()("(*) Indicates required fields"))
    ))
  }
}
