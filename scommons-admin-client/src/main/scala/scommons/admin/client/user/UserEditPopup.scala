package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.admin.client.api.user.UserDetailsData
import scommons.client.ui._
import scommons.client.ui.popup.{Modal, ModalProps}
import scommons.client.util.ActionsData

case class UserEditPopupProps(show: Boolean,
                              title: String,
                              onSave: UserDetailsData => Unit,
                              onCancel: () => Unit,
                              initialData: UserDetailsData)

object UserEditPopup extends UiComponent[UserEditPopupProps] {

  private case class UserEditPopupState(data: UserDetailsData,
                                        actionCommands: Set[String],
                                        opened: Boolean = false)

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, UserEditPopupState](
    getInitialState = { self =>
      val props = self.props.wrapped

      UserEditPopupState(props.initialData, getActionCommands(props.initialData))
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
        buttons = List(Buttons.SAVE.copy(
          image = ButtonImagesCss.dbSave,
          disabledImage = ButtonImagesCss.dbSaveDisabled,
          primary = true
        ), Buttons.CANCEL),
        actions = ActionsData(self.state.actionCommands, _ => {
          case Buttons.SAVE.command => onSave()
          case _ => props.onCancel()
        }),
        onClose = props.onCancel,
        onOpen = { () =>
          self.setState(_.copy(opened = true))
        }
      ))(
        <(UserEditPanel())(^.wrapped := UserEditPanelProps(
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

  private def getActionCommands(data: UserDetailsData): Set[String] = {
    if (data.user.login.trim.nonEmpty
      && data.user.password.nonEmpty
      && data.profile.firstName.trim.nonEmpty
      && data.profile.lastName.trim.nonEmpty
      && data.profile.email.trim.nonEmpty
      && data.profile.phone.forall(_.trim.nonEmpty)) {
      enabledActions
    }
    else disabledActions
  }
}
