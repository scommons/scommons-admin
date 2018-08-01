package scommons.admin.client.system.action

import io.github.shogowada.scalajs.reactjs.redux.Action
import scommons.admin.client.api.system.SystemData

case class SystemUpdatedAction(data: SystemData) extends Action
