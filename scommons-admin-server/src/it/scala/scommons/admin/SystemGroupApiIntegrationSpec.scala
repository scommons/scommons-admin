package scommons.admin

import java.util.UUID

import org.scalatest.DoNotDiscover
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.api.ApiStatus

@DoNotDiscover
class SystemGroupApiIntegrationSpec extends BaseAdminIntegrationSpec {

  "getSystemGroupById" should "fail if no such SystemGroup" in {
    //when & then
    callSystemGroupGetById(12345, SystemGroupNotFound) shouldBe None
  }

  "listSystemGroups" should "return list ordered by name" in {
    //given
    removeAllSystemGroups()
    
    val companies = List(
      createRandomSystemGroup(),
      createRandomSystemGroup(),
      createRandomSystemGroup(),
      createRandomSystemGroup()
    ).sortBy(_.name)
    
    //when & then
    callSystemGroupList() shouldBe companies
  }

  "createSystemGroup" should "fail if SystemGroup with such name already exists" in {
    //given
    val existing = createRandomSystemGroup()
    val data = SystemGroupData(
      None,
      existing.name
    )

    //when & then
    callSystemGroupCreate(data, SystemGroupAlreadyExists) shouldBe None
  }

  it should "create fresh new SystemGroup" in {
    //given
    val data = SystemGroupData(
      None,
      s"  ${UUID.randomUUID()}  "
    )

    //when
    val result = callSystemGroupCreate(data)

    //then
    result.id should not be None
    result.name shouldBe data.name.trim

    assertSystemGroup(result, callSystemGroupGetById(result.id.get))
  }

  "updateSystemGroup" should "fail if SystemGroup doesn't exist" in {
    //given
    val data = SystemGroupData(
      Some(12345),
      s"${UUID.randomUUID()}"
    )

    //when & then
    callSystemGroupUpdate(data, SystemGroupNotFound) shouldBe None
  }

  it should "fail if SystemGroup with such name already exists" in {
    //given
    val existing = createRandomSystemGroup()
    val data = SystemGroupData(
      createRandomSystemGroup().id,
      existing.name
    )

    //when & then
    callSystemGroupUpdate(data, SystemGroupAlreadyExists) shouldBe None
  }

  it should "fail with BadRequest if SystemGroup name is blank" in {
    //given
    val existing = createRandomSystemGroup()
    val data = SystemGroupData(
      existing.id,
      " "
    )

    //when & then
    callSystemGroupUpdate(data, ApiStatus(400, "name is blank")) shouldBe None
  }

  it should "fail if SystemGroup already updated" in {
    //given
    val existing = createRandomSystemGroup()
    val data = SystemGroupData(
      existing.id,
      existing.name
    )
    callSystemGroupUpdate(data)

    //when & then
    callSystemGroupUpdate(data, SystemGroupAlreadyUpdated) shouldBe None
  }

  it should "update existing SystemGroup" in {
    //given
    val existing = createRandomSystemGroup()
    val data = SystemGroupData(
      existing.id,
      s"  ${UUID.randomUUID()}  "
    )

    //when
    val result = callSystemGroupUpdate(data)

    //then
    result.id shouldBe existing.id
    result.name shouldBe data.name.trim

    assertSystemGroup(result, callSystemGroupGetById(result.id.get))
    
    result.createdAt shouldBe existing.createdAt
    result.version.get shouldBe existing.version.get + 1
  }

  private def assertSystemGroup(result: SystemGroupData, expected: SystemGroupData): Unit = {
    inside (result) {
      case SystemGroupData(id, name, updatedAt, createdAt, version) =>
        id shouldBe expected.id
        name shouldBe expected.name
        updatedAt.get.getMillis should be >= createdAt.get.getMillis
        createdAt.get.getMillis should be > 0L
        version.get should be >= 1
    }
  }
}
