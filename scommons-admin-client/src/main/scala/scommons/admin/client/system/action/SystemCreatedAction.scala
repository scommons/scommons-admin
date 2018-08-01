package scommons.admin.client.system.action

import io.github.shogowada.scalajs.reactjs.redux.Action
import scommons.admin.client.api.system.SystemData

case class SystemCreatedAction(data: SystemData) extends Action
