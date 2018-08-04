package scommons.admin.client.role.action

import scommons.admin.client.api.role.RoleListResp
import scommons.client.task.{FutureTask, TaskAction}

case class RoleListFetchAction(task: FutureTask[RoleListResp]) extends TaskAction
