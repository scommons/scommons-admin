package scommons.admin.client.user

import scommons.admin.client.api.user.{UserCompanyData, UserDetailsData}
import scommons.admin.client.company.CompanyActions
import scommons.client.ui._
import scommons.client.ui.select.{SearchSelect, SearchSelectProps, SelectData}
import scommons.react._
import scommons.react.redux.Dispatch

import scala.concurrent.ExecutionContext.Implicits.global

case class UserEditPanelProps(dispatch: Dispatch,
                              actions: CompanyActions,
                              initialData: UserDetailsData,
                              requestFocus: Boolean,
                              onChange: UserDetailsData => Unit,
                              onEnter: () => Unit)

object UserEditPanel extends FunctionComponent[UserEditPanelProps] {

  private[user] var textFieldComp: UiComponent[TextFieldProps] = TextField
  private[user] var passwordFieldComp: UiComponent[PasswordFieldProps] = PasswordField
  private[user] var searchSelectComp: UiComponent[SearchSelectProps] = SearchSelect

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    val data = props.initialData

    <.div(^.className := "form-horizontal")(
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Login"),
        <.div(^.className := "controls")(
          <(textFieldComp())(^.wrapped := TextFieldProps(
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
          <(passwordFieldComp())(^.wrapped := PasswordFieldProps(
            password = data.user.password,
            onChange = { value =>
              props.onChange(data.copy(user = data.user.copy(password = value)))
            },
            onEnter = props.onEnter
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*Company"),
        <.div(^.className := "controls")(
          <(searchSelectComp())(^.wrapped := SearchSelectProps(
            selected =
              if (data.user.company.id == -1) None
              else Some(SelectData(data.user.company.id.toString, data.user.company.name)),
            onChange = { value =>
              val v = value.getOrElse(SelectData("-1", ""))
              props.onChange(data.copy(user = data.user.copy(
                company = UserCompanyData(v.value.toInt, v.label)
              )))
            },
            onLoad = { inputValue =>
              props.actions.companyListFetch(props.dispatch, Some(0), Some(inputValue)).task.future.map { resp =>
                resp.dataList.toList.flatMap { list =>
                  list.map { data =>
                    SelectData(data.id.get.toString, data.name)
                  }
                }
              }
            }
          ))()
        )
      ),
      <.div(^.className := "control-group")(
        <.label(^.className := "control-label")("*First Name"),
        <.div(^.className := "controls")(
          <(textFieldComp())(^.wrapped := TextFieldProps(
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
          <(textFieldComp())(^.wrapped := TextFieldProps(
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
          <(textFieldComp())(^.wrapped := TextFieldProps(
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
          <(textFieldComp())(^.wrapped := TextFieldProps(
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
      <.p()(<.small()("(*) Indicates required fields"))
    )
  }
}
