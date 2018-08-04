package scommons.admin.client.role.action

import io.github.shogowada.scalajs.reactjs.redux.Action
import scommons.admin.client.api.role.RoleData

case class RoleCreatedAction(data: RoleData) extends Action
