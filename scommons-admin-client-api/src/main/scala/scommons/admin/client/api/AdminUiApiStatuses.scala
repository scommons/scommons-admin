package scommons.admin.client.api

import scommons.api.ApiStatus

object AdminUiApiStatuses {

  ////////////////////////////////////////////////////////////////////////////////////////
  // companies

  val CompanyNotFound = ApiStatus(1001, "Company not found")
  val CompanyAlreadyExists = ApiStatus(1002, "Company with such name already exists")

  ////////////////////////////////////////////////////////////////////////////////////////
  // systems/groups

  val SystemGroupNotFound = ApiStatus(1011, "SystemGroup not found")
  val SystemGroupAlreadyExists = ApiStatus(1012, "SystemGroup with such name already exists")
  val SystemGroupAlreadyUpdated = ApiStatus(1013, "SystemGroup was already updated by another user, try refresh")
}
