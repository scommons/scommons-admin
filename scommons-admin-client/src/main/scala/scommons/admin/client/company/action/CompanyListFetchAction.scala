package scommons.admin.client.company.action

import scommons.admin.client.api.company.CompanyListResp
import scommons.client.task.{FutureTask, TaskAction}

case class CompanyListFetchAction(task: FutureTask[CompanyListResp],
                                  offset: Option[Int]) extends TaskAction
