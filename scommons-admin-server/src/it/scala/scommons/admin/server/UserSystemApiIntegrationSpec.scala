package scommons.admin.server

import org.scalatest.DoNotDiscover
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.system.SystemData
import scommons.admin.client.api.user.system.{UserSystemData, UserSystemUpdateReq}

@DoNotDiscover
class UserSystemApiIntegrationSpec extends BaseAdminIntegrationSpec {

  "listUserSystems" should "fail if no such User" in {
    //when & then
    callUserSystemList(12345, UserNotFound) shouldBe None
  }

  it should "return list ordered by name" in {
    //given
    val company = createRandomCompany()
    val user = createRandomUser(company).user
    val group = createRandomSystemGroup()

    createRandomSystem(group.id.get)
    createRandomSystem(group.id.get)
    
    val selectedSystems = {
      val systems = Set(
        createRandomSystem(group.id.get),
        createRandomSystem(group.id.get)
      ).map(convertToUserSystem)
      
      systems.foldLeft(user) { (user, us) =>
        callUserSystemAdd(user.id.get, UserSystemUpdateReq(Set(us.id), user.version.get)).user
      }
      systems.map(s => s.copy(isSelected = true))
    }
    val otherSystems = callSystemList().map(convertToUserSystem)
      .filterNot(s => selectedSystems.exists(_.id == s.id))
    val expectedSystems = (otherSystems ++ selectedSystems).sortBy(s => s.name)
    
    //when
    val result = callUserSystemList(user.id.get)
    
    //then
    val expectedUser = callUserGetById(user.id.get).user
    result.systems shouldBe expectedSystems
    result.user shouldBe expectedUser
  }

  "addUserSystem" should "fail if no such User" in {
    //when & then
    callUserSystemAdd(12345, UserSystemUpdateReq(Set(1), 1), UserNotFound) shouldBe None
  }

  it should "fail if User already updated" in {
    //given
    val company = createRandomCompany()
    val user = createRandomUser(company).user
    val group = createRandomSystemGroup()

    val s = convertToUserSystem(createRandomSystem(group.id.get))
    val s2 = convertToUserSystem(createRandomSystem(group.id.get))
    val updated = callUserSystemAdd(user.id.get, UserSystemUpdateReq(
      Set(s.id),
      user.version.get
    ))

    //when & then
    callUserSystemAdd(user.id.get, UserSystemUpdateReq(
      Set(s2.id),
      user.version.get
    ), UserAlreadyUpdated) shouldBe None

    callUserSystemList(user.id.get) shouldBe updated
  }

  it should "add systems to the given user" in {
    //given
    val company = createRandomCompany()
    val user = createRandomUser(company).user
    val group = createRandomSystemGroup()
    
    createRandomSystem(group.id.get)

    val systemsToAdd = Set(
      createRandomSystem(group.id.get),
      createRandomSystem(group.id.get)
    ).map(convertToUserSystem)

    val currSystems = callSystemList().map(convertToUserSystem)
    val data = UserSystemUpdateReq(
      systemsToAdd.map(_.id),
      user.version.get
    )

    //when
    val result = callUserSystemAdd(user.id.get, data)

    //then
    result.systems shouldBe currSystems.map {
      case us if systemsToAdd.exists(_.id == us.id) => us.copy(isSelected = true)
      case us => us
    }.sortBy(_.name)
    
    result.user.version shouldBe user.version.map(_ + 1)
  }

  it should "add systems to updated user" in {
    //given
    val company = createRandomCompany()
    val user = createRandomUser(company).user
    val group = createRandomSystemGroup()

    val s = convertToUserSystem(createRandomSystem(group.id.get))
    val s2 = convertToUserSystem(createRandomSystem(group.id.get))
    val systemsToAdd = Set(s, s2)
    val currSystems = callSystemList().map(convertToUserSystem)
    
    val updated = callUserSystemAdd(user.id.get, UserSystemUpdateReq(
      Set(s.id),
      user.version.get
    ))

    //when
    val result = callUserSystemAdd(user.id.get, UserSystemUpdateReq(
      Set(s2.id),
      updated.user.version.get
    ))

    //then
    result.systems shouldBe currSystems.map {
      case us if systemsToAdd.exists(_.id == us.id) => us.copy(isSelected = true)
      case us => us
    }.sortBy(_.name)

    result.user.version shouldBe updated.user.version.map(_ + 1)
  }

  "removeUserSystem" should "fail if no such User" in {
    //when & then
    callUserSystemRemove(12345, UserSystemUpdateReq(Set(1), 1), UserNotFound) shouldBe None
  }

  it should "fail if User already updated" in {
    //given
    val company = createRandomCompany()
    val user = createRandomUser(company).user
    val group = createRandomSystemGroup()

    val s = convertToUserSystem(createRandomSystem(group.id.get))
    val s2 = convertToUserSystem(createRandomSystem(group.id.get))
    val systemsToAdd = Set(s, s2)
    val updated = callUserSystemAdd(user.id.get, UserSystemUpdateReq(
      systemsToAdd.map(_.id),
      user.version.get
    ))
    
    //when & then
    callUserSystemRemove(user.id.get, UserSystemUpdateReq(
      Set(s2.id),
      user.version.get
    ), UserAlreadyUpdated) shouldBe None

    callUserSystemList(user.id.get) shouldBe updated
  }

  it should "remove systems from the given user" in {
    //given
    val company = createRandomCompany()
    val user = createRandomUser(company).user
    val group = createRandomSystemGroup()

    val s = convertToUserSystem(createRandomSystem(group.id.get))
    val s2 = convertToUserSystem(createRandomSystem(group.id.get))
    val s3 = convertToUserSystem(createRandomSystem(group.id.get))
    val systemsToAdd = Set(s, s2, s3)
    val added = callUserSystemAdd(user.id.get, UserSystemUpdateReq(
      systemsToAdd.map(_.id),
      user.version.get
    ))
    
    val systemsToRemove = Set(s, s2)
    val currSystems = callSystemList().map(convertToUserSystem)
    
    val data = UserSystemUpdateReq(
      systemsToRemove.map(_.id),
      added.user.version.get
    )

    //when
    val result = callUserSystemRemove(user.id.get, data)

    //then
    result.systems shouldBe currSystems.map {
      case us if us.id == s3.id => us.copy(isSelected = true)
      case us => us
    }.sortBy(_.name)

    result.user.version shouldBe added.user.version.map(_ + 1)
  }

  it should "remove systems from updated user" in {
    //given
    val company = createRandomCompany()
    val user = createRandomUser(company).user
    val group = createRandomSystemGroup()

    val s = convertToUserSystem(createRandomSystem(group.id.get))
    val s2 = convertToUserSystem(createRandomSystem(group.id.get))
    val s3 = convertToUserSystem(createRandomSystem(group.id.get))
    val systemsToAdd = Set(s, s2, s3)
    val added = callUserSystemAdd(user.id.get, UserSystemUpdateReq(
      systemsToAdd.map(_.id),
      user.version.get
    ))

    val currSystems = callSystemList().map(convertToUserSystem)

    val updated = callUserSystemRemove(user.id.get, UserSystemUpdateReq(
      Set(s.id),
      added.user.version.get
    ))

    //when
    val result = callUserSystemRemove(user.id.get, UserSystemUpdateReq(
      Set(s2.id),
      updated.user.version.get
    ))

    //then
    result.systems shouldBe currSystems.map {
      case us if us.id == s3.id => us.copy(isSelected = true)
      case us => us
    }.sortBy(_.name)

    result.user.version shouldBe updated.user.version.map(_ + 1)
  }
  
  private def convertToUserSystem(system: SystemData): UserSystemData = {
    UserSystemData(system.id.get, system.name, isSelected = false)
  }
}
