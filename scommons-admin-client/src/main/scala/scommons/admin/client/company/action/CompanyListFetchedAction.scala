package scommons.admin.client.company.action

import io.github.shogowada.scalajs.reactjs.redux.Action
import scommons.admin.client.api.company.CompanyData

case class CompanyListFetchedAction(dataList: List[CompanyData],
                                    totalCount: Option[Int]) extends Action
