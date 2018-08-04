package scommons.admin

import org.scalatest.DoNotDiscover
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.system.SystemData
import scommons.api.ApiStatus

@DoNotDiscover
class SystemApiIntegrationSpec extends BaseAdminIntegrationSpec {

  "getSystemById" should "fail if no such System" in {
    //when & then
    callSystemGetById(12345, SystemNotFound) shouldBe None
  }

  "listSystems" should "return list ordered by name" in {
    //given
    val parent1 = createRandomSystemGroup()
    val parent2 = createRandomSystemGroup()
    //removeAllSystems()
    
    val systems = List(
      createRandomSystem(parent1.id.get),
      createRandomSystem(parent2.id.get),
      createRandomSystem(parent2.id.get),
      createRandomSystem(parent1.id.get)
    ).sortBy(_.name)
    
    //when & then
    callSystemList().filterNot(_.id.contains(1)) shouldBe systems
  }

  "createSystem" should "fail if System with such name already exists" in {
    //given
    val parent = createRandomSystemGroup()
    val existing = createRandomSystem(parent.id.get)
    val data = existing.copy(
      id = None,
      version = None
    )

    //when & then
    callSystemCreate(data, SystemAlreadyExists) shouldBe None
  }

  it should "create fresh new System" in {
    //given
    val parent = createRandomSystemGroup()
    val data = SystemData(
      id = None,
      name = s"  ${System.nanoTime()}  ",
      password = "test password",
      title = "Some title",
      url = "http://test.com/some/url",
      parentId = parent.id.get
    )

    //when
    val result = callSystemCreate(data)

    //then
    result.id should not be None
    result.name shouldBe data.name.trim

    assertSystem(result, callSystemGetById(result.id.get))
  }

  "updateSystem" should "fail if System doesn't exist" in {
    //given
    val parent = createRandomSystemGroup()
    val existing = createRandomSystem(parent.id.get)
    val data = existing.copy(
      id = Some(12345)
    )

    //when & then
    callSystemUpdate(data, SystemNotFound) shouldBe None
  }

  it should "fail if System with such name already exists" in {
    //given
    val parent = createRandomSystemGroup()
    val existing = createRandomSystem(parent.id.get)
    val data = existing.copy(
      id = createRandomSystem(parent.id.get).id
    )

    //when & then
    callSystemUpdate(data, SystemAlreadyExists) shouldBe None
  }

  it should "fail with BadRequest if System name is blank" in {
    //given
    val parent = createRandomSystemGroup()
    val existing = createRandomSystem(parent.id.get)
    val data = existing.copy(
      name = " "
    )

    //when & then
    callSystemUpdate(data, ApiStatus(400, "name is blank")) shouldBe None
  }

  it should "fail if System already updated" in {
    //given
    val parent = createRandomSystemGroup()
    val existing = createRandomSystem(parent.id.get)
    val data = existing.copy(
      name = s"${System.nanoTime()}"
    )
    callSystemUpdate(data)

    //when & then
    callSystemUpdate(data, SystemAlreadyUpdated) shouldBe None
  }

  it should "update existing System" in {
    //given
    val parent = createRandomSystemGroup()
    val existing = createRandomSystem(parent.id.get)
    val data = existing.copy(
      name = s"  ${System.nanoTime()}  "
    )

    //when
    val result = callSystemUpdate(data)

    //then
    result.id shouldBe existing.id
    result.name shouldBe data.name.trim

    assertSystem(result, callSystemGetById(result.id.get))
    
    result.createdAt shouldBe existing.createdAt
    result.version.get shouldBe existing.version.get + 1
  }

  it should "not update read-only fields" in {
    //given
    val parent = createRandomSystemGroup()
    val parent2 = createRandomSystemGroup()
    val existing = createRandomSystem(parent.id.get)
    val data = existing.copy(
      name = s"  ${System.nanoTime()}  ",
      parentId = parent2.id.get
    )

    //when
    val result = callSystemUpdate(data)

    //then
    result.parentId shouldBe existing.parentId

    assertSystem(result, callSystemGetById(result.id.get))
  }

  it should "update updated System" in {
    //given
    val parent = createRandomSystemGroup()
    val existing = createRandomSystem(parent.id.get)
    val data = callSystemUpdate(existing.copy(
      name = s"${System.nanoTime()}"
    ))

    //when
    val result = callSystemUpdate(data)

    //then
    result.id shouldBe existing.id
    result.name shouldBe data.name.trim

    assertSystem(result, callSystemGetById(result.id.get))
  }

  private def assertSystem(result: SystemData, expected: SystemData): Unit = {
    inside (result) {
      case SystemData(id, name, password, title, url, parentId, updatedAt, createdAt, version) =>
        id shouldBe expected.id
        name shouldBe expected.name
        password shouldBe expected.password
        title shouldBe expected.title
        url shouldBe expected.url
        parentId shouldBe expected.parentId
        updatedAt.get.getMillis should be >= createdAt.get.getMillis
        createdAt.get.getMillis should be > 0L
        version.get should be >= 1
    }
  }
}
