package scommons.admin.client.company.action

import scommons.admin.client.api.company.CompanyResp
import scommons.client.task.{FutureTask, TaskAction}

case class CompanyUpdateAction(task: FutureTask[CompanyResp]) extends TaskAction
