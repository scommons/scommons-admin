package scommons.admin.server

import org.scalatest.DoNotDiscover
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.role.permission.RolePermissionUpdateReq

@DoNotDiscover
class RolePermissionApiIntegrationSpec extends BaseAdminIntegrationSpec {

  "listRolePermissions" should "fail if no such Role" in {
    //when & then
    callRolePermissionList(12345, RoleNotFound) shouldBe None
  }

  it should "return list ordered by (parentId, title)" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role = createRandomRole(systemId)

    val p0 = createRandomPermission(systemId, isNode = true)
    val p1 = createRandomPermission(systemId, isNode = true)
    val p2 = createRandomPermission(systemId, parentId = Some(p1.id), enabledForRoleIds = Set(role.id.get))
    val p3 = createRandomPermission(systemId, parentId = Some(p1.id), isNode = true)
    val p4 = createRandomPermission(systemId, parentId = Some(p3.id), enabledForRoleIds = Set(role.id.get))
    val p5 = createRandomPermission(systemId, parentId = Some(p3.id), enabledForRoleIds = Set(role.id.get))
    val permissions = List(p0, p1, p2, p3, p4, p5).sortBy(p => (p.parentId, p.title))
    
    //when
    val result = callRolePermissionList(role.id.get)
    
    //then
    result.permissions shouldBe permissions
    result.role shouldBe role
  }

  "addRolePermission" should "fail if no such Role" in {
    //when & then
    callRolePermissionAdd(12345, RolePermissionUpdateReq(Set(1), 1), RoleNotFound) shouldBe None
  }

  it should "fail if Role already updated" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role = createRandomRole(systemId)

    val p = createRandomPermission(systemId)
    val p2 = createRandomPermission(systemId)
    val updated = callRolePermissionAdd(role.id.get, RolePermissionUpdateReq(
      Set(p.id),
      role.version.get
    ))

    //when & then
    callRolePermissionAdd(role.id.get, RolePermissionUpdateReq(
      Set(p2.id),
      role.version.get
    ), RoleAlreadyUpdated) shouldBe None

    callRolePermissionList(role.id.get) shouldBe updated
  }

  it should "add permissions to the given role" in {
    //given
    val group = createRandomSystemGroup()
    val system0 = createRandomSystem(group.id.get)
    val p0 = createRandomPermission(system0.id.get)
    val systemId = createRandomSystem(group.id.get).id.get
    val role = createRandomRole(systemId)
    
    val p1 = createRandomPermission(systemId)
    val p2 = createRandomPermission(systemId, parentId = Some(p1.id), isNode = true)
    val p3 = createRandomPermission(systemId, parentId = Some(p2.id))
    val p4 = createRandomPermission(systemId, parentId = Some(p2.id))
    p0.isEnabled shouldBe false
    p1.isEnabled shouldBe false
    p2.isEnabled shouldBe false
    p3.isEnabled shouldBe false
    p4.isEnabled shouldBe false
    val data = RolePermissionUpdateReq(
      Set(p0.id, p2.id, p3.id, p4.id),
      role.version.get
    )

    //when
    val result = callRolePermissionAdd(role.id.get, data)

    //then
    result.permissions shouldBe List(
      p1,
      p2,
      p3.copy(isEnabled = true),
      p4.copy(isEnabled = true)
    ).sortBy(p => (p.parentId, p.title))

    result.role.version shouldBe role.version.map(_ + 1)
  }

  it should "add permissions to updated role" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role = createRandomRole(systemId)

    val p = createRandomPermission(systemId)
    val p2 = createRandomPermission(systemId)
    val updated = callRolePermissionAdd(role.id.get, RolePermissionUpdateReq(
      Set(p.id),
      role.version.get
    ))

    //when
    val result = callRolePermissionAdd(role.id.get, RolePermissionUpdateReq(
      Set(p2.id),
      updated.role.version.get
    ))

    //then
    result.permissions shouldBe List(
      p.copy(isEnabled = true),
      p2.copy(isEnabled = true)
    ).sortBy(p => (p.parentId, p.title))

    result.role.version shouldBe updated.role.version.map(_ + 1)
  }

  "removeRolePermission" should "fail if no such Role" in {
    //when & then
    callRolePermissionRemove(12345, RolePermissionUpdateReq(Set(1), 1), RoleNotFound) shouldBe None
  }

  it should "fail if Role already updated" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role = createRandomRole(systemId)

    val p = createRandomPermission(systemId, enabledForRoleIds = Set(role.id.get))
    val p2 = createRandomPermission(systemId, enabledForRoleIds = Set(role.id.get))
    val updated = callRolePermissionRemove(role.id.get, RolePermissionUpdateReq(
      Set(p.id),
      role.version.get
    ))

    //when & then
    callRolePermissionRemove(role.id.get, RolePermissionUpdateReq(
      Set(p2.id),
      role.version.get
    ), RoleAlreadyUpdated) shouldBe None

    callRolePermissionList(role.id.get) shouldBe updated
  }

  it should "remove permissions from the given role" in {
    //given
    val group = createRandomSystemGroup()
    val system0 = createRandomSystem(group.id.get)
    val p0 = createRandomPermission(system0.id.get)
    val systemId = createRandomSystem(group.id.get).id.get
    val role = createRandomRole(systemId)

    val p1 = createRandomPermission(systemId)
    val p2 = createRandomPermission(systemId, parentId = Some(p1.id), isNode = true)
    val p3 = createRandomPermission(systemId, parentId = Some(p2.id), enabledForRoleIds = Set(role.id.get))
    val p4 = createRandomPermission(systemId, parentId = Some(p2.id), enabledForRoleIds = Set(role.id.get))
    p0.isEnabled shouldBe false
    p1.isEnabled shouldBe false
    p2.isEnabled shouldBe false
    p3.isEnabled shouldBe true
    p4.isEnabled shouldBe true
    val data = RolePermissionUpdateReq(
      Set(p0.id, p2.id, p3.id, p4.id),
      role.version.get
    )

    //when
    val result = callRolePermissionRemove(role.id.get, data)

    //then
    result.permissions shouldBe List(
      p1,
      p2,
      p3.copy(isEnabled = false),
      p4.copy(isEnabled = false)
    ).sortBy(p => (p.parentId, p.title))

    result.role.version shouldBe role.version.map(_ + 1)
  }

  it should "remove permissions from updated role" in {
    //given
    val group = createRandomSystemGroup()
    val systemId = createRandomSystem(group.id.get).id.get
    val role = createRandomRole(systemId)

    val p = createRandomPermission(systemId, enabledForRoleIds = Set(role.id.get))
    val p2 = createRandomPermission(systemId, enabledForRoleIds = Set(role.id.get))
    val updated = callRolePermissionRemove(role.id.get, RolePermissionUpdateReq(
      Set(p.id),
      role.version.get
    ))

    //when
    val result = callRolePermissionRemove(role.id.get, RolePermissionUpdateReq(
      Set(p2.id),
      updated.role.version.get
    ))

    //then
    result.permissions shouldBe List(
      p.copy(isEnabled = false),
      p2.copy(isEnabled = false)
    ).sortBy(p => (p.parentId, p.title))

    result.role.version shouldBe updated.role.version.map(_ + 1)
  }
}
