package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.elements.ReactElement
import scommons.admin.client.api.system.SystemData
import scommons.client.ui.popup.{SaveCancelPopup, SaveCancelPopupProps}

case class SystemEditPopupProps(title: String,
                                initialData: SystemData,
                                onSave: SystemData => Unit,
                                onCancel: () => Unit) extends SaveCancelPopupProps {

  type DataType = SystemData

  def isSaveEnabled(data: SystemData): Boolean = {
    (data.name.trim.nonEmpty
      && data.password.nonEmpty
      && data.title.trim.nonEmpty
      && data.url.trim.nonEmpty)
  }

  def render(data: SystemData,
             requestFocus: Boolean,
             onChange: SystemData => Unit,
             onSave: () => Unit): ReactElement = {

    <(SystemEditPanel())(^.wrapped := SystemEditPanelProps(
      readOnly = false,
      initialData = data,
      requestFocus = requestFocus,
      onChange = onChange,
      onEnter = onSave
    ))()
  }
}

object SystemEditPopup extends SaveCancelPopup[SystemEditPopupProps]
