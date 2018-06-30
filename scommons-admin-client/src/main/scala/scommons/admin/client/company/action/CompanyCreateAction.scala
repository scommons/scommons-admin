package scommons.admin.client.company.action

import scommons.admin.client.api.company.CompanyResp
import scommons.client.task.{FutureTask, TaskAction}

case class CompanyCreateAction(task: FutureTask[CompanyResp]) extends TaskAction
