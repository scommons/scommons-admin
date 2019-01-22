package scommons.admin.server

import org.scalatest.DoNotDiscover
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.role.RoleData
import scommons.api.ApiStatus

@DoNotDiscover
class RoleApiIntegrationSpec extends BaseAdminIntegrationSpec {

  "getRoleById" should "fail if no such Role" in {
    //when & then
    callRoleGetById(12345, RoleNotFound) shouldBe None
  }

  "listRoles" should "return list ordered by name" in {
    //given
    val group = createRandomSystemGroup()
    val system1 = createRandomSystem(group.id.get)
    val system2 = createRandomSystem(group.id.get)
    //removeAllRoles()
    
    val roles = List(
      createRandomRole(system1.id.get),
      createRandomRole(system2.id.get),
      createRandomRole(system2.id.get),
      createRandomRole(system1.id.get)
    ).sortBy(_.title)
    
    //when & then
    callRoleList() shouldBe roles
  }

  "createRole" should "fail if Role with such name already exists" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    val existing = createRandomRole(system.id.get)
    val data = existing.copy(
      id = None,
      version = None
    )

    //when & then
    callRoleCreate(data, RoleAlreadyExists) shouldBe None
  }

  it should "create fresh new Role" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    val data = RoleData(
      id = None,
      systemId = system.id.get,
      title = s"  ${System.nanoTime()}  "
    )

    //when
    val result = callRoleCreate(data)

    //then
    result.id should not be None
    result.title shouldBe data.title.trim

    assertRole(result, callRoleGetById(result.id.get))
  }

  "updateRole" should "fail if Role doesn't exist" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    val existing = createRandomRole(system.id.get)
    val data = existing.copy(
      id = Some(12345)
    )

    //when & then
    callRoleUpdate(data, RoleNotFound) shouldBe None
  }

  it should "fail if Role with such title already exists" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    val existing = createRandomRole(system.id.get)
    val data = existing.copy(
      id = createRandomRole(system.id.get).id
    )

    //when & then
    callRoleUpdate(data, RoleAlreadyExists) shouldBe None
  }

  it should "fail with BadRequest if Role title is blank" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    val existing = createRandomRole(system.id.get)
    val data = existing.copy(
      title = " "
    )

    //when & then
    callRoleUpdate(data, ApiStatus(400, "title is blank")) shouldBe None
  }

  it should "fail if Role already updated" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    val existing = createRandomRole(system.id.get)
    val data = existing.copy(
      title = s"${System.nanoTime()}"
    )
    callRoleUpdate(data)

    //when & then
    callRoleUpdate(data, RoleAlreadyUpdated) shouldBe None
  }

  it should "update existing Role" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    val existing = createRandomRole(system.id.get)
    val data = existing.copy(
      title = s"  ${System.nanoTime()}  "
    )

    //when
    val result = callRoleUpdate(data)

    //then
    result.id shouldBe existing.id
    result.title shouldBe data.title.trim

    assertRole(result, callRoleGetById(result.id.get))
    
    result.createdAt shouldBe existing.createdAt
    result.version.get shouldBe existing.version.get + 1
  }

  it should "not update read-only fields" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    val system2 = createRandomSystem(group.id.get)
    createRandomRole(system.id.get)
    val existing = createRandomRole(system.id.get)
    val data = existing.copy(
      systemId = system2.id.get,
      title = s"  ${System.nanoTime()}  "
    )
    val bitIndex = roleDao.getById(existing.id.get).futureValue.get.bitIndex

    //when
    val result = callRoleUpdate(data)

    //then
    result.systemId shouldBe existing.systemId
    roleDao.getById(result.id.get).futureValue.get.bitIndex shouldBe bitIndex

    assertRole(result, callRoleGetById(result.id.get))
  }

  it should "update updated Role" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    val existing = createRandomRole(system.id.get)
    val data = callRoleUpdate(existing.copy(
      title = s"${System.nanoTime()}"
    ))

    //when
    val result = callRoleUpdate(data)

    //then
    result.id shouldBe existing.id
    result.title shouldBe data.title.trim

    assertRole(result, callRoleGetById(result.id.get))
  }

  private def assertRole(result: RoleData, expected: RoleData): Unit = {
    inside (result) {
      case RoleData(id, systemId, title, updatedAt, createdAt, version) =>
        id shouldBe expected.id
        systemId shouldBe expected.systemId
        title shouldBe expected.title
        updatedAt.get.getMillis should be >= createdAt.get.getMillis
        createdAt.get.getMillis should be > 0L
        version.get should be >= 1
    }
  }
}
