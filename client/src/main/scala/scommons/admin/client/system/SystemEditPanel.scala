package scommons.admin.client.system

import scommons.admin.client.api.system.SystemData
import scommons.client.ui._
import scommons.react._

case class SystemEditPanelProps(readOnly: Boolean,
                                initialData: SystemData,
                                requestFocus: Boolean,
                                onChange: SystemData => Unit,
                                onEnter: () => Unit)

object SystemEditPanel extends FunctionComponent[SystemEditPanelProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    val data = props.initialData

    <.>()(
      <.div()(
        <.label()("Name"),
        <(TextField())(^.wrapped := TextFieldProps(
          text = data.name,
          onChange = { value =>
            props.onChange(data.copy(name = value))
          },
          requestFocus = props.requestFocus,
          requestSelect = props.requestFocus,
          className = Some("input-xlarge"),
          onEnter = props.onEnter,
          readOnly = props.readOnly
        ))()
      ),
      <.div()(
        <.label()("Password"),
        <(PasswordField())(^.wrapped := PasswordFieldProps(
          password = data.password,
          onChange = { value =>
            props.onChange(data.copy(password = value))
          },
          className = Some("input-xlarge"),
          onEnter = props.onEnter,
          readOnly = props.readOnly
        ))()
      ),
      <.div()(
        <.label()("Title"),
        <(TextField())(^.wrapped := TextFieldProps(
          text = data.title,
          onChange = { value =>
            props.onChange(data.copy(title = value))
          },
          className = Some("input-xlarge"),
          onEnter = props.onEnter,
          readOnly = props.readOnly
        ))()
      ),
      <.div()(
        <.label()("URL"),
        <(TextField())(^.wrapped := TextFieldProps(
          text = data.url,
          onChange = { value =>
            props.onChange(data.copy(url = value))
          },
          className = Some("input-xlarge"),
          onEnter = props.onEnter,
          readOnly = props.readOnly
        ))()
      )
    )
  }
}
