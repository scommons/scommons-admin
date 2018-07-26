package scommons.admin.client.system.group.action

import io.github.shogowada.scalajs.reactjs.redux.Action
import scommons.admin.client.api.system.group.SystemGroupData

case class SystemGroupCreatedAction(data: SystemGroupData) extends Action
