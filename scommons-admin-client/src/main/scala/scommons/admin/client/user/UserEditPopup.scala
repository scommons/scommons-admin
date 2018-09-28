package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.elements.ReactElement
import scommons.admin.client.api.user.UserDetailsData
import scommons.client.ui.popup.{SaveCancelPopup, SaveCancelPopupProps}

case class UserEditPopupProps(show: Boolean,
                              title: String,
                              initialData: UserDetailsData,
                              onSave: UserDetailsData => Unit,
                              onCancel: () => Unit) extends SaveCancelPopupProps {

  type DataType = UserDetailsData

  def isSaveEnabled(data: UserDetailsData): Boolean = {
    (data.user.login.trim.nonEmpty
      && data.user.password.nonEmpty
      && data.profile.firstName.trim.nonEmpty
      && data.profile.lastName.trim.nonEmpty
      && data.profile.email.trim.nonEmpty
      && data.profile.phone.forall(_.trim.nonEmpty))
  }

  def render(data: UserDetailsData,
             requestFocus: Boolean,
             onChange: UserDetailsData => Unit,
             onSave: () => Unit): ReactElement = {
    
    <(UserEditPanel())(^.wrapped := UserEditPanelProps(
      initialData = data,
      requestFocus = requestFocus,
      onChange = onChange,
      onEnter = onSave
    ))()
  }
}

object UserEditPopup extends SaveCancelPopup[UserEditPopupProps]
