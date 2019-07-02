package scommons.admin.client.user

import scommons.admin.client.api.user.UserProfileData
import scommons.client.ui._
import scommons.react._

case class UserProfilePanelProps(data: UserProfileData)

object UserProfilePanel extends FunctionComponent[UserProfilePanelProps] {

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
          <(TextField())(^.wrapped := TextFieldProps(
            text = props.data.firstName,
            onChange = onChange,
            readOnly = true
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("Last Name"),
        <.div(^.className := "controls")(
          <(TextField())(^.wrapped := TextFieldProps(
            text = props.data.lastName,
            onChange = onChange,
            readOnly = true
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("E-mail"),
        <.div(^.className := "controls")(
          <(TextField())(^.wrapped := TextFieldProps(
            text = props.data.email,
            onChange = onChange,
            readOnly = true
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("Phone"),
        <.div(^.className := "controls")(
          <(TextField())(^.wrapped := TextFieldProps(
            text = props.data.phone.getOrElse(""),
            onChange = onChange,
            readOnly = true
          ))()
        )
      )
    )
  }
}
