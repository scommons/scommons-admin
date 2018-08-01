package scommons.admin.client.system.action

import scommons.admin.client.api.system.SystemListResp
import scommons.client.task.{FutureTask, TaskAction}

case class SystemListFetchAction(task: FutureTask[SystemListResp]) extends TaskAction
