package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.admin.client.api.system.SystemData
import scommons.client.ui._

case class SystemEditPanelProps(readOnly: Boolean,
                                initialData: SystemData,
                                requestFocus: Boolean,
                                onChange: SystemData => Unit,
                                onEnter: () => Unit)

object SystemEditPanel extends UiComponent[SystemEditPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped
    val data = props.initialData

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
      ))(),
      
      <.label()("Password"),
      <(PasswordField())(^.wrapped := PasswordFieldProps(
        password = data.password,
        onChange = { value =>
          props.onChange(data.copy(password = value))
        },
        className = Some("input-xlarge"),
        onEnter = props.onEnter,
        readOnly = props.readOnly
      ))(),
      
      <.label()("Title"),
      <(TextField())(^.wrapped := TextFieldProps(
        text = data.title,
        onChange = { value =>
          props.onChange(data.copy(title = value))
        },
        className = Some("input-xlarge"),
        onEnter = props.onEnter,
        readOnly = props.readOnly
      ))(),
      
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
  }
}
