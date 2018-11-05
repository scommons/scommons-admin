package scommons.admin.client.api

import scommons.admin.client.api.company._
import scommons.admin.client.api.role._
import scommons.admin.client.api.role.permission._
import scommons.admin.client.api.system._
import scommons.admin.client.api.system.group._
import scommons.admin.client.api.user._
import scommons.admin.client.api.user.system._
import scommons.api.http.ApiHttpClient

import scala.concurrent.Future

class AdminUiApiClient(client: ApiHttpClient)
  extends CompanyApi
    with SystemGroupApi
    with SystemApi
    with RoleApi
    with RolePermissionApi
    with UserApi
    with UserSystemApi {

  ////////////////////////////////////////////////////////////////////////////////////////
  // companies

  def getCompanyById(id: Int): Future[CompanyResp] = {
    client.execGet[CompanyResp](s"/companies/$id")
  }
  
  def listCompanies(offset: Option[Int] = None,
                    limit: Option[Int] = None,
                    symbols: Option[String] = None): Future[CompanyListResp] = {

    client.execGet[CompanyListResp]("/companies", params = ApiHttpClient.queryParams(
      "offset" -> offset,
      "limit" -> limit,
      "symbols" -> symbols
    ))
  }

  def createCompany(data: CompanyData): Future[CompanyResp] = {
    client.execPost[CompanyData, CompanyResp]("/companies", data)
  }

  def updateCompany(data: CompanyData): Future[CompanyResp] = {
    client.execPut[CompanyData, CompanyResp]("/companies", data)
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // systems/groups

  def getSystemGroupById(id: Int): Future[SystemGroupResp] = {
    client.execGet[SystemGroupResp](s"/systems/groups/$id")
  }

  def listSystemGroups(): Future[SystemGroupListResp] = {
    client.execGet[SystemGroupListResp]("/systems/groups")
  }

  def createSystemGroup(data: SystemGroupData): Future[SystemGroupResp] = {
    client.execPost[SystemGroupData, SystemGroupResp]("/systems/groups", data)
  }

  def updateSystemGroup(data: SystemGroupData): Future[SystemGroupResp] = {
    client.execPut[SystemGroupData, SystemGroupResp]("/systems/groups", data)
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // systems

  def getSystemById(id: Int): Future[SystemResp] = {
    client.execGet[SystemResp](s"/systems/$id")
  }

  def listSystems(): Future[SystemListResp] = {
    client.execGet[SystemListResp]("/systems")
  }

  def createSystem(data: SystemData): Future[SystemResp] = {
    client.execPost[SystemData, SystemResp]("/systems", data)
  }

  def updateSystem(data: SystemData): Future[SystemResp] = {
    client.execPut[SystemData, SystemResp]("/systems", data)
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // roles

  def getRoleById(id: Int): Future[RoleResp] = {
    client.execGet[RoleResp](s"/roles/$id")
  }

  def listRoles(): Future[RoleListResp] = {
    client.execGet[RoleListResp]("/roles")
  }

  def createRole(data: RoleData): Future[RoleResp] = {
    client.execPost[RoleData, RoleResp]("/roles", data)
  }

  def updateRole(data: RoleData): Future[RoleResp] = {
    client.execPut[RoleData, RoleResp]("/roles", data)
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // roles/permissions

  def listRolePermissions(roleId: Int): Future[RolePermissionResp] = {
    client.execGet[RolePermissionResp](s"/roles/$roleId/permissions")
  }

  def addRolePermissions(roleId: Int, data: RolePermissionUpdateReq): Future[RolePermissionResp] = {
    client.execPost[RolePermissionUpdateReq, RolePermissionResp](s"/roles/$roleId/permissions", data)
  }

  def removeRolePermissions(roleId: Int, data: RolePermissionUpdateReq): Future[RolePermissionResp] = {
    client.execPut[RolePermissionUpdateReq, RolePermissionResp](s"/roles/$roleId/permissions", data)
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // users

  def getUserById(id: Int): Future[UserDetailsResp] = {
    client.execGet[UserDetailsResp](s"/users/$id")
  }

  def listUsers(offset: Option[Int] = None,
                limit: Option[Int] = None,
                symbols: Option[String] = None): Future[UserListResp] = {

    client.execGet[UserListResp]("/users", params = ApiHttpClient.queryParams(
      "offset" -> offset,
      "limit" -> limit,
      "symbols" -> symbols
    ))
  }

  def createUser(data: UserDetailsData): Future[UserDetailsResp] = {
    client.execPost[UserDetailsData, UserDetailsResp]("/users", data)
  }

  def updateUser(data: UserDetailsData): Future[UserDetailsResp] = {
    client.execPut[UserDetailsData, UserDetailsResp]("/users", data)
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // users/systems

  def listUserSystems(userId: Int): Future[UserSystemResp] = {
    client.execGet[UserSystemResp](s"/users/$userId/systems")
  }

  def addUserSystems(userId: Int, data: UserSystemUpdateReq): Future[UserSystemResp] = {
    client.execPost[UserSystemUpdateReq, UserSystemResp](s"/users/$userId/systems", data)
  }

  def removeUserSystems(userId: Int, data: UserSystemUpdateReq): Future[UserSystemResp] = {
    client.execPut[UserSystemUpdateReq, UserSystemResp](s"/users/$userId/systems", data)
  }
}
