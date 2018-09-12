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
  
  ////////////////////////////////////////////////////////////////////////////////////////
  // systems

  val SystemNotFound = ApiStatus(1021, "System not found")
  val SystemAlreadyExists = ApiStatus(1022, "System with such name already exists")
  val SystemAlreadyUpdated = ApiStatus(1023, "System was already updated by another user, try refresh")

  ////////////////////////////////////////////////////////////////////////////////////////
  // roles

  val RoleNotFound = ApiStatus(1031, "Role not found")
  val RoleAlreadyExists = ApiStatus(1032, "Role with such title already exists")
  val RoleAlreadyUpdated = ApiStatus(1033, "Role was already updated by another user, try refresh")
  
  ////////////////////////////////////////////////////////////////////////////////////////
  // users

  val UserNotFound = ApiStatus(1041, "User not found")
  val UserLoginAlreadyExists = ApiStatus(1042, "User with such login already exists")
  val UserEmailAlreadyExists = ApiStatus(1043, "User with such email already exists")
  val UserAlreadyUpdated = ApiStatus(1044, "User was already updated by another user, try refresh")
}
