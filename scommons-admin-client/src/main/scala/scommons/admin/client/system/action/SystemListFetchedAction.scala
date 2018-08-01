package scommons.admin.client.system.action

import io.github.shogowada.scalajs.reactjs.redux.Action
import scommons.admin.client.api.system.SystemData

case class SystemListFetchedAction(dataList: List[SystemData]) extends Action
