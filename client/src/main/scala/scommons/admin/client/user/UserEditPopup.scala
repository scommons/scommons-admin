package scommons.admin.client.user

import scommons.admin.client.api.user.UserDetailsData
import scommons.admin.client.company.CompanyActions
import scommons.client.ui.popup.{SaveCancelPopup, SaveCancelPopupProps}
import scommons.react._
import scommons.react.redux.Dispatch

case class UserEditPopupProps(dispatch: Dispatch,
                              actions: CompanyActions,
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
    
    <(UserEditPopup.userEditPanelComp())(^.wrapped := UserEditPanelProps(
      dispatch = dispatch,
      actions = actions,
      initialData = data,
      requestFocus = requestFocus,
      onChange = onChange,
      onEnter = onSave
    ))()
  }
}

object UserEditPopup extends SaveCancelPopup[UserEditPopupProps] {

  private[user] var userEditPanelComp: UiComponent[UserEditPanelProps] = UserEditPanel
}
