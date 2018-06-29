package scommons.admin.client.api

import scommons.api.ApiStatus

object AdminUiApiStatuses {

  ////////////////////////////////////////////////////////////////////////////////////////
  // companies

  val CompanyNotFound = ApiStatus(1001, "Company not found")
  val CompanyAlreadyExists = ApiStatus(1002, "Company with such name already exists")
}
