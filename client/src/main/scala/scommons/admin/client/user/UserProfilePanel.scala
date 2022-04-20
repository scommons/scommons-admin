package scommons.admin.client.user

import scommons.admin.client.api.user.UserProfileData
import scommons.client.ui._
import scommons.react._

case class UserProfilePanelProps(data: UserProfileData)

object UserProfilePanel extends FunctionComponent[UserProfilePanelProps] {

  private[user] var firstNameComp: UiComponent[TextFieldProps] = TextField
  private[user] var lastNameComp: UiComponent[TextFieldProps] = TextField
  private[user] var emailComp: UiComponent[TextFieldProps] = TextField
  private[user] var phoneComp: UiComponent[TextFieldProps] = TextField

  override protected def create(): ReactClass = {
    ReactMemo[Props](super.create(), { (prevProps, nextProps) =>
      prevProps.wrapped == nextProps.wrapped
    })
  }
  
  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    val onChange = (_: String) => ()

    <.div(^.className := "form-horizontal")(
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("First Name"),
        <.div(^.className := "controls")(
          <(firstNameComp())(^.wrapped := TextFieldProps(
            text = props.data.firstName,
            onChange = onChange,
            readOnly = true
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("Last Name"),
        <.div(^.className := "controls")(
          <(lastNameComp())(^.wrapped := TextFieldProps(
            text = props.data.lastName,
            onChange = onChange,
            readOnly = true
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("E-mail"),
        <.div(^.className := "controls")(
          <(emailComp())(^.wrapped := TextFieldProps(
            text = props.data.email,
            onChange = onChange,
            readOnly = true
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("Phone"),
        <.div(^.className := "controls")(
          <(phoneComp())(^.wrapped := TextFieldProps(
            text = props.data.phone.getOrElse(""),
            onChange = onChange,
            readOnly = true
          ))()
        )
      )
    )
  }
}
