package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.admin.client.api.user.UserProfileData
import scommons.client.ui._

case class UserProfilePanelProps(data: UserProfileData)

object UserProfilePanel extends UiComponent[UserProfilePanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped
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
