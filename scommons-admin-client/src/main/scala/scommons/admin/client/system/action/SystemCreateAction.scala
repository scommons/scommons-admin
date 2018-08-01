package scommons.admin.client.system.action

import scommons.admin.client.api.system.SystemResp
import scommons.client.task.{FutureTask, TaskAction}

case class SystemCreateAction(task: FutureTask[SystemResp]) extends TaskAction
