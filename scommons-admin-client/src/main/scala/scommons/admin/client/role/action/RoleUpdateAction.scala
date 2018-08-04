package scommons.admin.client.role.action

import scommons.admin.client.api.role.RoleResp
import scommons.client.task.{FutureTask, TaskAction}

case class RoleUpdateAction(task: FutureTask[RoleResp]) extends TaskAction
