package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.admin.client.api.user.{UserCompanyData, UserDetailsData}
import scommons.client.ui._

case class UserEditPanelProps(initialData: UserDetailsData,
                              requestFocus: Boolean,
                              onChange: UserDetailsData => Unit,
                              onEnter: () => Unit)

object UserEditPanel extends UiComponent[UserEditPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped
    val data = props.initialData

    <.div(^.className := "form-horizontal")(
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Login"),
        <.div(^.className := "controls")(
          <(TextField())(^.wrapped := TextFieldProps(
            text = data.user.login,
            onChange = { value =>
              props.onChange(data.copy(user = data.user.copy(login = value)))
            },
            requestFocus = props.requestFocus,
            requestSelect = props.requestFocus,
            onEnter = props.onEnter
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Password"),
        <.div(^.className := "controls")(
          <(PasswordField())(^.wrapped := PasswordFieldProps(
            password = data.user.password,
            onChange = { value =>
              props.onChange(data.copy(user = data.user.copy(password = value)))
            },
            onEnter = props.onEnter
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*First Name"),
        <.div(^.className := "controls")(
          <(TextField())(^.wrapped := TextFieldProps(
            text = data.profile.firstName,
            onChange = { value =>
              props.onChange(data.copy(profile = data.profile.copy(firstName = value)))
            },
            onEnter = props.onEnter
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Last Name"),
        <.div(^.className := "controls")(
          <(TextField())(^.wrapped := TextFieldProps(
            text = data.profile.lastName,
            onChange = { value =>
              props.onChange(data.copy(profile = data.profile.copy(lastName = value)))
            },
            onEnter = props.onEnter
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*E-mail"),
        <.div(^.className := "controls")(
          <(TextField())(^.wrapped := TextFieldProps(
            text = data.profile.email,
            onChange = { value =>
              props.onChange(data.copy(profile = data.profile.copy(email = value)))
            },
            onEnter = props.onEnter
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("Phone"),
        <.div(^.className := "controls")(
          <(TextField())(^.wrapped := TextFieldProps(
            text = data.profile.phone.getOrElse(""),
            onChange = { value =>
              props.onChange(data.copy(profile = data.profile.copy(
                phone = if (value.isEmpty) None else Some(value)
              )))
            },
            onEnter = props.onEnter
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Company"),
        <.div(^.className := "controls")(
          <(TextField())(^.wrapped := TextFieldProps(
            text = data.user.company.name,
            onChange = { value =>
              props.onChange(data.copy(user = data.user.copy(
                company = UserCompanyData(1, value) //TODO: select company
              )))
            },
            onEnter = props.onEnter,
            readOnly = true
          ))()
        )
      ),
      <.p()(<.small()("(*) Indicates required fields"))
    )
  }
}
