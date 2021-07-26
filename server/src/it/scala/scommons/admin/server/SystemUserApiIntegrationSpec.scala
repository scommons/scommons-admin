package scommons.admin.server

import java.text.Collator
import java.util.Locale

import org.joda.time.DateTimeZone
import org.scalatest.DoNotDiscover
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.system.user._
import scommons.admin.client.api.user.system.UserSystemUpdateReq
import scommons.admin.domain.SystemUser

@DoNotDiscover
class SystemUserApiIntegrationSpec extends BaseAdminIntegrationSpec {

  "listSystemUsers" should "fail if no such System" in {
    //when & then
    callSystemUserList(12345, expectedStatus = SystemNotFound) shouldBe {
      (Nil, None)
    }
  }

  it should "return paginated, filtered list, ordered by login" in {
    //given
    val collator = Collator.getInstance(Locale.US)
    implicit val o: Ordering[String] = new Ordering[String] {
      def compare(x: String, y: String): Int = collator.compare(x, y)
    }
    val company = createRandomCompany()
    val systemGroup = createRandomSystemGroup()
    val emptySystemId = createRandomSystem(systemGroup.id.get).id.get
    val systemId = createRandomSystem(systemGroup.id.get).id.get
    val symbols = s"${System.nanoTime()}SeArCH"
    val users = {
      val users = List(
        createRandomUser(company).user,
        createRandomUser(company, partOfLogin = Some(symbols.toUpperCase)).user,
        createRandomUser(company, partOfLogin = Some(symbols)).user,
        createRandomUser(company, partOfLogin = Some(symbols.toLowerCase)).user,
        createRandomUser(company, partOfLogin = Some(symbols)).user
      )
      users.foreach { user =>
        callUserSystemAdd(user.id.get, UserSystemUpdateReq(Set(systemId), user.version.get))
      }

      val systemUsers = systemUserDao.listBySystemId(systemId).futureValue
      users.map { user =>
        val su = systemUsers.find(_.userId == user.id.get).get
        SystemUserData(
          userId = su.userId,
          login = user.login,
          lastLoginDate = user.lastLoginDate,
          updatedAt = su.updatedAt.toDateTime(DateTimeZone.getDefault),
          createdAt = su.createdAt.toDateTime(DateTimeZone.getDefault),
          version = su.version
        )
      }.sortBy(_.login)
    }

    def listAndAssert(offset: Option[Int],
                      limit: Option[Int],
                      partOfLogin: Option[String]): Unit = {

      callSystemUserList(systemId, offset, limit, partOfLogin) shouldBe {
        val filteredList = users
          .filter(_.login.toLowerCase.contains(partOfLogin.map(_.toLowerCase).getOrElse("")))
        val list = filteredList
          .slice(offset.getOrElse(0), offset.getOrElse(0) + limit.getOrElse(10))
        
        (list, if (offset.isDefined) None else Some(filteredList.size))
      }
    }

    //when & then
    callSystemUserList(emptySystemId, offset = None, limit = None, symbols = None) shouldBe {
      (Nil, Some(0))
    }
    listAndAssert(offset = None, limit = None, partOfLogin = None)
    listAndAssert(offset = None, limit = Some(1), partOfLogin = None)
    listAndAssert(offset = None, limit = Some(2), partOfLogin = Some(symbols))
    listAndAssert(offset = Some(1), limit = None, partOfLogin = None)
    listAndAssert(offset = Some(2), limit = None, partOfLogin = Some(symbols))
    listAndAssert(offset = Some(1), limit = Some(2), partOfLogin = None)
    listAndAssert(offset = Some(2), limit = Some(2), partOfLogin = Some(symbols))
  }

  "listSystemUserRoles" should "fail if no such System" in {
    //when & then
    callSystemUserRoleList(12345, 123456, SystemNotFound) shouldBe None
  }

  it should "fail if no such SystemUser" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get

    //when & then
    callSystemUserRoleList(systemId, 123456, SystemUserNotFound) shouldBe None
  }

  it should "return roles and permissions ordered by (parentId, title)" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get

    val role0 = createRandomRole(systemId)
    val role1 = createRandomRole(systemId)
    val role2 = createRandomRole(systemId)
    val role3 = createRandomRole(systemId)
    val expectedRoles = List(
      role0 -> false,
      createRandomRole(systemId) -> false,
      role1 -> true,
      role2 -> true,
      role3 -> true
    ).map {
      case (r, isSelected) => convertToSystemUserRole(r, isSelected)
    }.sortBy(s => s.title)
    
    val systemUser = createRandomSystemUser(systemId, expectedRoles.collect {
      case r if r.isSelected => r.id
    })
    
    val p0 = createRandomPermission(systemId, isNode = true)
    val p1 = createRandomPermission(systemId, isNode = true)
    val p2 = createRandomPermission(systemId, parentId = Some(p1.id), enabledForRoleIds = Set(role0.id.get))
    val p3 = createRandomPermission(systemId, parentId = Some(p1.id), isNode = true)
    val p4 = createRandomPermission(systemId, parentId = Some(p3.id), enabledForRoleIds = Set(
      role1.id.get, role2.id.get
    ))
    val p5 = createRandomPermission(systemId, parentId = Some(p3.id), enabledForRoleIds = Set(role3.id.get))
    val permissions = List(
      p0,
      p1,
      p2.copy(isEnabled = false),
      p3,
      p4,
      p5
    ).sortBy(p => (p.parentId, p.title))

    //when
    val result = callSystemUserRoleList(systemId, systemUser.userId)

    //then
    val expectedSystemUser = callSystemUserList(systemId)._1.head
    inside(result) { case SystemUserRoleRespData(resRoles, resPermissions, resSU) =>
      resRoles shouldBe expectedRoles
      resPermissions shouldBe permissions
      resSU shouldBe expectedSystemUser
    }
  }

  "addSystemUserRole" should "fail if no such System" in {
    //when & then
    callSystemUserRoleAdd(12345, 123456, SystemUserRoleUpdateReq(Set(1), 1), SystemNotFound) shouldBe None
  }

  it should "fail if no such SystemUser" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get

    //when & then
    callSystemUserRoleAdd(systemId, 123456, SystemUserRoleUpdateReq(Set(1), 1), SystemUserNotFound) shouldBe None
  }

  it should "fail if SystemUser already updated" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role1 = createRandomRole(systemId)
    val role2 = createRandomRole(systemId)

    val systemUser = createRandomSystemUser(systemId, Nil)
    val updated = callSystemUserRoleAdd(systemId, systemUser.userId, SystemUserRoleUpdateReq(
      Set(role1.id.get),
      systemUser.version
    ))

    //when & then
    callSystemUserRoleAdd(systemId, systemUser.userId, SystemUserRoleUpdateReq(
      Set(role2.id.get),
      systemUser.version
    ), SystemUserAlreadyUpdated) shouldBe None

    callSystemUserRoleList(systemId, systemUser.userId) shouldBe updated
  }

  it should "add roles to the given SystemUser" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role0 = createRandomRole(systemId)
    val role1 = createRandomRole(systemId)
    val role2 = createRandomRole(systemId)
    val expectedRoles = List(
      role0 -> false,
      createRandomRole(systemId) -> false,
      role1 -> true,
      role2 -> true
    ).map {
      case (r, isSelected) => convertToSystemUserRole(r, isSelected)
    }.sortBy(s => s.title)

    val systemUser = createRandomSystemUser(systemId, Nil)
    
    val p1 = createRandomPermission(systemId)
    val p2 = createRandomPermission(systemId, parentId = Some(p1.id))
    val p3 = createRandomPermission(systemId, parentId = Some(p2.id), enabledForRoleIds = Set(role0.id.get))
    val p4 = createRandomPermission(systemId, parentId = Some(p2.id), enabledForRoleIds = Set(role1.id.get))
    val p5 = createRandomPermission(systemId, parentId = Some(p2.id), enabledForRoleIds = Set(role2.id.get))
    val data = SystemUserRoleUpdateReq(
      Set(role1.id.get, role2.id.get),
      systemUser.version
    )

    //when
    val result = callSystemUserRoleAdd(systemId, systemUser.userId, data)

    //then
    val expectedPermissions = List(
      p1,
      p2,
      p3.copy(isEnabled = false),
      p4.copy(isEnabled = true),
      p5.copy(isEnabled = true)
    ).sortBy(p => (p.parentId, p.title))

    val expectedSystemUser = callSystemUserList(systemId)._1.head
    inside(result) { case SystemUserRoleRespData(resRoles, resPermissions, resSU) =>
      resRoles shouldBe expectedRoles
      resPermissions shouldBe expectedPermissions
      resSU shouldBe expectedSystemUser
      resSU.version shouldBe (systemUser.version + 1)
    }
  }

  it should "add roles to updated SystemUser" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role1 = createRandomRole(systemId)
    val role2 = createRandomRole(systemId)

    val systemUser = createRandomSystemUser(systemId, Nil)
    val updated = callSystemUserRoleAdd(systemId, systemUser.userId, SystemUserRoleUpdateReq(
      Set(role1.id.get),
      systemUser.version
    ))

    //when
    val result = callSystemUserRoleAdd(systemId, systemUser.userId, SystemUserRoleUpdateReq(
      Set(role2.id.get),
      updated.systemUser.version
    ))

    //then
    result shouldBe callSystemUserRoleList(systemId, systemUser.userId)
    result.systemUser.version shouldBe (updated.systemUser.version + 1)
  }

  "removeSystemUserRole" should "fail if no such System" in {
    //when & then
    callSystemUserRoleRemove(12345, 123456, SystemUserRoleUpdateReq(Set(1), 1), SystemNotFound) shouldBe None
  }

  it should "fail if no such SystemUser" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get

    //when & then
    callSystemUserRoleRemove(systemId, 123456, SystemUserRoleUpdateReq(Set(1), 1), SystemUserNotFound) shouldBe None
  }

  it should "fail if SystemUser already updated" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role1 = createRandomRole(systemId)
    val role2 = createRandomRole(systemId)

    val systemUser = createRandomSystemUser(systemId, List(role1.id.get, role2.id.get))
    val updated = callSystemUserRoleRemove(systemId, systemUser.userId, SystemUserRoleUpdateReq(
      Set(role1.id.get),
      systemUser.version
    ))

    //when & then
    callSystemUserRoleRemove(systemId, systemUser.userId, SystemUserRoleUpdateReq(
      Set(role2.id.get),
      systemUser.version
    ), SystemUserAlreadyUpdated) shouldBe None

    callSystemUserRoleList(systemId, systemUser.userId) shouldBe updated
  }

  it should "remove roles from the given SystemUser" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role0 = createRandomRole(systemId)
    val role1 = createRandomRole(systemId)
    val role2 = createRandomRole(systemId)
    val expectedRoles = List(
      role0 -> true,
      createRandomRole(systemId) -> false,
      role1 -> false,
      role2 -> false
    ).map {
      case (r, isSelected) => convertToSystemUserRole(r, isSelected)
    }.sortBy(s => s.title)

    val systemUser = createRandomSystemUser(systemId, List(
      role0.id.get,
      role1.id.get,
      role2.id.get
    ))

    val p1 = createRandomPermission(systemId)
    val p2 = createRandomPermission(systemId, parentId = Some(p1.id))
    val p3 = createRandomPermission(systemId, parentId = Some(p2.id), enabledForRoleIds = Set(role0.id.get))
    val p4 = createRandomPermission(systemId, parentId = Some(p2.id), enabledForRoleIds = Set(role1.id.get))
    val p5 = createRandomPermission(systemId, parentId = Some(p2.id), enabledForRoleIds = Set(role2.id.get))
    val data = SystemUserRoleUpdateReq(
      Set(role1.id.get, role2.id.get),
      systemUser.version
    )

    //when
    val result = callSystemUserRoleRemove(systemId, systemUser.userId, data)

    //then
    val expectedPermissions = List(
      p1,
      p2,
      p3.copy(isEnabled = true),
      p4.copy(isEnabled = false),
      p5.copy(isEnabled = false)
    ).sortBy(p => (p.parentId, p.title))

    val expectedSystemUser = callSystemUserList(systemId)._1.head
    inside(result) { case SystemUserRoleRespData(resRoles, resPermissions, resSU) =>
      resRoles shouldBe expectedRoles
      resPermissions shouldBe expectedPermissions
      resSU shouldBe expectedSystemUser
      resSU.version shouldBe (systemUser.version + 1)
    }
  }

  it should "remove roles from updated SystemUser" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role1 = createRandomRole(systemId)
    val role2 = createRandomRole(systemId)

    val systemUser = createRandomSystemUser(systemId, List(role1.id.get, role2.id.get))
    val updated = callSystemUserRoleRemove(systemId, systemUser.userId, SystemUserRoleUpdateReq(
      Set(role1.id.get),
      systemUser.version
    ))

    //when
    val result = callSystemUserRoleRemove(systemId, systemUser.userId, SystemUserRoleUpdateReq(
      Set(role2.id.get),
      updated.systemUser.version
    ))

    //then
    result shouldBe callSystemUserRoleList(systemId, systemUser.userId)
    result.systemUser.version shouldBe (updated.systemUser.version + 1)
  }

  private def convertToSystemUserRole(role: RoleData, isSelected: Boolean): SystemUserRoleData = {
    SystemUserRoleData(role.id.get, role.title, isSelected)
  }

  private def createRandomSystemUser(systemId: Int, roleIds: List[Int]): SystemUser = {
    val company = createRandomCompany()
    val user = createRandomUser(company).user
    callUserSystemAdd(user.id.get, UserSystemUpdateReq(Set(systemId), user.version.get))

    val su = inside(systemUserDao.getById(systemId, user.id.get).futureValue) {
      case Some(su) => su
    }
    if (roleIds.nonEmpty) {
      val bitIndexes = roleDao.listBySystemId(systemId).futureValue.collect {
        case role if roleIds.contains(role.id) => role.bitIndex
      }
      val roles = bitIndexes.foldLeft(0L)((roles, bit) => roles | (1L << bit))
      systemUserDao.update(su.copy(roles = roles)).futureValue shouldBe true

      val updated = inside(systemUserDao.getById(systemId, user.id.get).futureValue) {
        case Some(updated) => updated
      }
      updated.roles shouldBe roles
      updated
    }
    else su
  }
}
