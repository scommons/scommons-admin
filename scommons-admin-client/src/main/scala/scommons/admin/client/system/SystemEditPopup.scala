package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.admin.client.api.system.SystemData
import scommons.client.ui.popup.{Modal, ModalProps}
import scommons.client.ui._
import scommons.client.util.ActionsData

case class SystemEditPopupProps(show: Boolean,
                                title: String,
                                onSave: SystemData => Unit,
                                onCancel: () => Unit,
                                initialData: SystemData)

object SystemEditPopup extends UiComponent[SystemEditPopupProps] {

  private case class SystemEditPopupState(data: SystemData,
                                          actionCommands: Set[String],
                                          opened: Boolean = false)

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, SystemEditPopupState](
    getInitialState = { self =>
      val props = self.props.wrapped

      SystemEditPopupState(props.initialData, getActionCommands(props.initialData))
    },
    componentWillReceiveProps = { (self, nextProps) =>
      val props = nextProps.wrapped
      if (self.props.wrapped != props) {
        self.setState(_.copy(
          data = props.initialData,
          actionCommands = getActionCommands(props.initialData),
          opened = false
        ))
      }
    },
    render = { self =>
      val props = self.props.wrapped

      val onSave = { () =>
        if (self.state.actionCommands.contains(Buttons.SAVE.command)) {
          props.onSave(self.state.data)
        }
      }

      <(Modal())(^.wrapped := ModalProps(props.show,
        header = Some(props.title),
        buttons = List(Buttons.SAVE.copy(primary = true), Buttons.CANCEL),
        actions = ActionsData(self.state.actionCommands, _ => {
          case Buttons.SAVE.command => onSave()
          case _ => props.onCancel()
        }),
        onClose = props.onCancel,
        onOpen = { () =>
          self.setState(_.copy(opened = true))
        }
      ))(
        <(SystemEditPanel())(^.wrapped := SystemEditPanelProps(
          readOnly = false,
          initialData = self.state.data,
          requestFocus = self.state.opened,
          onChange = { data =>
            self.setState(_.copy(data = data, actionCommands = getActionCommands(data)))
          },
          onEnter = onSave
        ))()
      )
    }
  )

  private val enabledActions = Set(Buttons.SAVE.command, Buttons.CANCEL.command)
  private val disabledActions = Set(Buttons.CANCEL.command)

  private def getActionCommands(data: SystemData): Set[String] = {
    if (data.name.trim.nonEmpty
      && data.password.nonEmpty
      && data.title.trim.nonEmpty
      && data.url.trim.nonEmpty) {
      enabledActions
    }
    else disabledActions
  }
}
