package scommons.admin.client.system.group.action

import scommons.admin.client.api.system.group.SystemGroupResp
import scommons.client.task.{FutureTask, TaskAction}

case class SystemGroupUpdateAction(task: FutureTask[SystemGroupResp]) extends TaskAction
