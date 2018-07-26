package scommons.admin.client.system.group.action

import scommons.admin.client.api.system.group.SystemGroupListResp
import scommons.client.task.{FutureTask, TaskAction}

case class SystemGroupListFetchAction(task: FutureTask[SystemGroupListResp]) extends TaskAction
