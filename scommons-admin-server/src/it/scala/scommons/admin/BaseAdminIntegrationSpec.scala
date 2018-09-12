package scommons.admin

import java.util.UUID

import akka.actor.ActorSystem
import org.scalatest._
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.ConfiguredServer
import scommons.admin.client.api.AdminUiApiClient
import scommons.admin.client.api.company.CompanyData
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.role.permission._
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.api.user._
import scommons.admin.domain.dao._
import scommons.admin.domain.{Permission, RolePermission}
import scommons.api.ApiStatus
import services.{CompanyService, RoleService}

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

trait BaseAdminIntegrationSpec extends FlatSpec
  with Matchers
  with ConfiguredServer
  with ScalaFutures
  with Inside
  with Eventually
  with BeforeAndAfterEach {

  implicit val defaultPatience: PatienceConfig = PatienceConfig(
    timeout = Span(10, Seconds),
    interval = Span(50, Millis)
  )

  private def inject[T: ClassTag]: T = app.injector.instanceOf[T]

  implicit lazy val ec: ExecutionContext = inject[ActorSystem].dispatcher
  
  protected lazy val companyService: CompanyService = inject[CompanyService]
  protected lazy val companyDao: CompanyDao = inject[CompanyDao]
  protected lazy val systemGroupDao: SystemGroupDao = inject[SystemGroupDao]
  protected lazy val systemDao: SystemDao = inject[SystemDao]
  protected lazy val roleDao: RoleDao = inject[RoleDao]
  protected lazy val permissionDao: PermissionDao = inject[PermissionDao]
  protected lazy val rolePermissionDao: RolePermissionDao = inject[RolePermissionDao]
  protected lazy val roleService: RoleService = inject[RoleService]
  protected lazy val userDao: UserDao = inject[UserDao]

  protected lazy val superUserId: Int = 1

  protected lazy val uiApiClient: AdminUiApiClient = inject[AdminUiApiClient]

  ////////////////////////////////////////////////////////////////////////////////////////
  // companies

  def removeAllCompanies(): Unit = {
    val futureResult = for {
      _ <- companyDao.deleteAll()
    } yield {
      ()
    }

    // wait for operation to complete
    futureResult.futureValue
  }

  def createRandomCompany(partOfName: Option[String] = None): CompanyData = {
    callCompanyCreate(CompanyData(None,
      if (partOfName.isDefined) s"${System.nanoTime()}-${partOfName.get}-random"
      else s"${UUID.randomUUID()} random name"
    ))
  }

  def callCompanyGetById(id: Int): CompanyData = {
    callCompanyGetById(id, ApiStatus.Ok).get
  }

  def callCompanyGetById(id: Int, expectedStatus: ApiStatus): Option[CompanyData] = {
    val resp = uiApiClient.getCompanyById(id).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callCompanyList(offset: Option[Int] = None,
                      limit: Option[Int] = None,
                      symbols: Option[String] = None): (List[CompanyData], Option[Int]) = {
    
    val resp = uiApiClient.listCompanies(offset, limit, symbols).futureValue
    resp.status shouldBe ApiStatus.Ok
    (resp.dataList.get, resp.totalCount)
  }

  def callCompanyCreate(data: CompanyData): CompanyData = {
    callCompanyCreate(data, ApiStatus.Ok).get
  }

  def callCompanyCreate(data: CompanyData, expectedStatus: ApiStatus): Option[CompanyData] = {
    val resp = uiApiClient.createCompany(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callCompanyUpdate(data: CompanyData): CompanyData = {
    callCompanyUpdate(data, ApiStatus.Ok).get
  }

  def callCompanyUpdate(data: CompanyData, expectedStatus: ApiStatus): Option[CompanyData] = {
    val resp = uiApiClient.updateCompany(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // systems/groups

  def removeAllSystemGroups(): Unit = {
    val futureResult = for {
      _ <- systemGroupDao.deleteAll()
    } yield {
      ()
    }

    // wait for operation to complete
    futureResult.futureValue
  }

  def createRandomSystemGroup(): SystemGroupData = {
    callSystemGroupCreate(SystemGroupData(
      id = None,
      name = s"${UUID.randomUUID()} random name"
    ))
  }

  def callSystemGroupGetById(id: Int): SystemGroupData = {
    callSystemGroupGetById(id, ApiStatus.Ok).get
  }

  def callSystemGroupGetById(id: Int, expectedStatus: ApiStatus): Option[SystemGroupData] = {
    val resp = uiApiClient.getSystemGroupById(id).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callSystemGroupList(): List[SystemGroupData] = {
    val resp = uiApiClient.listSystemGroups().futureValue
    resp.status shouldBe ApiStatus.Ok
    resp.dataList.get
  }

  def callSystemGroupCreate(data: SystemGroupData): SystemGroupData = {
    callSystemGroupCreate(data, ApiStatus.Ok).get
  }

  def callSystemGroupCreate(data: SystemGroupData, expectedStatus: ApiStatus): Option[SystemGroupData] = {
    val resp = uiApiClient.createSystemGroup(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callSystemGroupUpdate(data: SystemGroupData): SystemGroupData = {
    callSystemGroupUpdate(data, ApiStatus.Ok).get
  }

  def callSystemGroupUpdate(data: SystemGroupData, expectedStatus: ApiStatus): Option[SystemGroupData] = {
    val resp = uiApiClient.updateSystemGroup(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////
  // systems

  def removeAllSystems(): Unit = {
    val futureResult = for {
      _ <- systemDao.deleteAll()
    } yield {
      ()
    }

    // wait for operation to complete
    futureResult.futureValue
  }

  def createRandomSystem(parentId: Int): SystemData = {
    callSystemCreate(SystemData(
      id = None,
      name = s"${System.nanoTime()} test name",
      password = s"${System.nanoTime()} test password",
      title = s"${System.nanoTime()} test title",
      url = s"http://${System.nanoTime()}.test.com/random/url",
      parentId = parentId
    ))
  }

  def callSystemGetById(id: Int): SystemData = {
    callSystemGetById(id, ApiStatus.Ok).get
  }

  def callSystemGetById(id: Int, expectedStatus: ApiStatus): Option[SystemData] = {
    val resp = uiApiClient.getSystemById(id).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callSystemList(): List[SystemData] = {
    val resp = uiApiClient.listSystems().futureValue
    resp.status shouldBe ApiStatus.Ok
    resp.dataList.get
  }

  def callSystemCreate(data: SystemData): SystemData = {
    callSystemCreate(data, ApiStatus.Ok).get
  }

  def callSystemCreate(data: SystemData, expectedStatus: ApiStatus): Option[SystemData] = {
    val resp = uiApiClient.createSystem(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callSystemUpdate(data: SystemData): SystemData = {
    callSystemUpdate(data, ApiStatus.Ok).get
  }

  def callSystemUpdate(data: SystemData, expectedStatus: ApiStatus): Option[SystemData] = {
    val resp = uiApiClient.updateSystem(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////
  // roles

  def createRandomRole(systemId: Int): RoleData = {
    callRoleCreate(RoleData(
      id = None,
      systemId = systemId,
      title = s"${System.nanoTime()} test title"
    ))
  }

  def callRoleGetById(id: Int): RoleData = {
    callRoleGetById(id, ApiStatus.Ok).get
  }

  def callRoleGetById(id: Int, expectedStatus: ApiStatus): Option[RoleData] = {
    val resp = uiApiClient.getRoleById(id).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callRoleList(): List[RoleData] = {
    val resp = uiApiClient.listRoles().futureValue
    resp.status shouldBe ApiStatus.Ok
    resp.dataList.get
  }

  def callRoleCreate(data: RoleData): RoleData = {
    callRoleCreate(data, ApiStatus.Ok).get
  }

  def callRoleCreate(data: RoleData, expectedStatus: ApiStatus): Option[RoleData] = {
    val resp = uiApiClient.createRole(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callRoleUpdate(data: RoleData): RoleData = {
    callRoleUpdate(data, ApiStatus.Ok).get
  }

  def callRoleUpdate(data: RoleData, expectedStatus: ApiStatus): Option[RoleData] = {
    val resp = uiApiClient.updateRole(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////
  // roles/permissions

  def createRandomPermission(systemId: Int,
                             isNode: Boolean = false,
                             parentId: Option[Int] = None,
                             enabledForRoleId: Option[Int] = None): RolePermissionData = {
    
    val name = s"${System.nanoTime()}-name"
    val permission = Permission(
      id = -1,
      systemId = systemId,
      name = name,
      title = s"$name Title",
      isNode = isNode,
      parentId = parentId
    )
    
    permissionDao.insert(Set(permission)).futureValue
    
    val created = permissionDao.list(systemId, None).map { permissions =>
      permissions.collectFirst {
        case (p, _) if p.name == name => RolePermissionData(
          id = p.id,
          parentId = p.parentId,
          isNode = p.isNode,
          title = p.title,
          isEnabled = false
        )
      }.getOrElse(
        throw new IllegalStateException(s"Can't find created permission with name: $name")
      )
    }.futureValue

    enabledForRoleId match {
      case None => created
      case Some(roleId) =>
        rolePermissionDao.insert(Set(RolePermission(roleId, created.id))).futureValue
        created.copy(isEnabled = true)
    }
  }

  def callRolePermissionList(roleId: Int): RolePermissionRespData = {
    callRolePermissionList(roleId, ApiStatus.Ok).get
  }
  
  def callRolePermissionList(roleId: Int, expectedStatus: ApiStatus): Option[RolePermissionRespData] = {
    val resp = uiApiClient.listRolePermissions(roleId).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callRolePermissionAdd(roleId: Int, data: RolePermissionUpdateReq): RolePermissionRespData = {
    callRolePermissionAdd(roleId, data, ApiStatus.Ok).get
  }

  def callRolePermissionAdd(roleId: Int,
                            data: RolePermissionUpdateReq,
                            expectedStatus: ApiStatus): Option[RolePermissionRespData] = {

    val resp = uiApiClient.addRolePermissions(roleId, data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callRolePermissionRemove(roleId: Int, data: RolePermissionUpdateReq): RolePermissionRespData = {
    callRolePermissionRemove(roleId, data, ApiStatus.Ok).get
  }

  def callRolePermissionRemove(roleId: Int,
                               data: RolePermissionUpdateReq,
                               expectedStatus: ApiStatus): Option[RolePermissionRespData] = {

    val resp = uiApiClient.removeRolePermissions(roleId, data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // users

  def createRandomUser(company: CompanyData, partOfLogin: Option[String] = None): UserDetailsData = {
    callUserCreate(UserDetailsData(
      user = UserData(
        id = None,
        company = UserCompanyData(company.id.get, company.name),
        login =
          if (partOfLogin.isDefined) s"${System.nanoTime()}-${partOfLogin.get}-rnd"
          else s"${System.nanoTime()}_rnd _login",
        password = s"${System.nanoTime()}_rnd _password",
        active = true
      ),
      profile = UserProfileData(
        email = s"${System.nanoTime()}_rnd@test.com",
        firstName = s"${System.nanoTime()} First Name",
        lastName = s"${System.nanoTime()} Last Name",
        phone = Some(s"${System.nanoTime()}".take(24))
      )
    ))
  }

  def callUserGetById(id: Int): UserDetailsData = {
    callUserGetById(id, ApiStatus.Ok).get
  }

  def callUserGetById(id: Int, expectedStatus: ApiStatus): Option[UserDetailsData] = {
    val resp = uiApiClient.getUserById(id).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callUserList(offset: Option[Int] = None,
                   limit: Option[Int] = None,
                   symbols: Option[String] = None): (List[UserData], Option[Int]) = {

    val resp = uiApiClient.listUsers(offset, limit, symbols).futureValue
    resp.status shouldBe ApiStatus.Ok
    (resp.dataList.get, resp.totalCount)
  }

  def callUserCreate(data: UserDetailsData): UserDetailsData = {
    callUserCreate(data, ApiStatus.Ok).get
  }

  def callUserCreate(data: UserDetailsData, expectedStatus: ApiStatus): Option[UserDetailsData] = {
    val resp = uiApiClient.createUser(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }

  def callUserUpdate(data: UserDetailsData): UserDetailsData = {
    callUserUpdate(data, ApiStatus.Ok).get
  }

  def callUserUpdate(data: UserDetailsData, expectedStatus: ApiStatus): Option[UserDetailsData] = {
    val resp = uiApiClient.updateUser(data).futureValue
    resp.status shouldBe expectedStatus
    resp.data
  }
}
