package scommons.admin.server

import java.text.Collator
import java.util.Locale

import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.DoNotDiscover
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.company.CompanyData
import scommons.admin.client.api.user._
import scommons.api.ApiStatus
import scommons.service.util.HashUtils

@DoNotDiscover
class UserApiIntegrationSpec extends BaseAdminIntegrationSpec {

  "getUserById" should "fail if no such company" in {
    //when & then
    callUserGetById(12345, UserNotFound) shouldBe None
  }

  "listUsers" should "return paginated, filtered list, ordered by login" in {
    //given
    val collator = Collator.getInstance(Locale.US)
    implicit val o: Ordering[String] = new Ordering[String] {
      def compare(x: String, y: String): Int = collator.compare(x, y)
    }
    val company = createRandomCompany()
    val symbols = s"${System.nanoTime()}SeArCH"
    val users = List(
      callUserGetById(superUserId),
      createRandomUser(company),
      createRandomUser(company, partOfLogin = Some(symbols.toUpperCase)),
      createRandomUser(company, partOfLogin = Some(symbols)),
      createRandomUser(company, partOfLogin = Some(symbols.toLowerCase)),
      createRandomUser(company, partOfLogin = Some(symbols))
    ).map(_.user).sortBy(_.login)

    def listAndAssert(offset: Option[Int],
                      limit: Option[Int],
                      partOfLogin: Option[String]): Unit = {

      callUserList(offset, limit, partOfLogin) shouldBe {
        val filteredList = users
          .filter(_.login.toLowerCase.contains(partOfLogin.map(_.toLowerCase).getOrElse("")))
        val list = filteredList
          .slice(offset.getOrElse(0), offset.getOrElse(0) + limit.getOrElse(10))
        
        (list, if (offset.isDefined) None else Some(filteredList.size))
      }
    }

    //when & then
    listAndAssert(offset = None, limit = None, partOfLogin = None)
    listAndAssert(offset = None, limit = Some(1), partOfLogin = None)
    listAndAssert(offset = None, limit = Some(2), partOfLogin = Some(symbols))
    listAndAssert(offset = Some(1), limit = None, partOfLogin = None)
    listAndAssert(offset = Some(2), limit = None, partOfLogin = Some(symbols))
    listAndAssert(offset = Some(1), limit = Some(2), partOfLogin = None)
    listAndAssert(offset = Some(2), limit = Some(2), partOfLogin = Some(symbols))
  }

  "createUser" should "fail if company doesn't exist" in {
    //given
    val company = CompanyData(Some(123), "Not existing")
    val data = UserDetailsData(
      user = UserData(
        id = None,
        company = UserCompanyData(company.id.get, company.name),
        login = s"${System.nanoTime()}_rnd _login",
        password = s"${System.nanoTime()}_rnd _password",
        active = true
      ),
      profile = UserProfileData(
        email = s"${System.nanoTime()}_rnd@test.com",
        firstName = s"${System.nanoTime()} First Name",
        lastName = s"${System.nanoTime()} Last Name",
        phone = Some(s"${System.nanoTime()}".take(24))
      )
    )

    //when & then
    callUserCreate(data, CompanyNotFound) shouldBe None
  }

  it should "fail if user with such login already exists" in {
    //given
    val company = createRandomCompany()
    val existing = createRandomUser(company)
    val data = existing.copy(
      user = existing.user.copy(
        id = None,
        version = None
      ),
      profile = existing.profile.copy(
        email = s"${System.nanoTime()}_rnd@test.com",
        version = None
      )
    )

    //when & then
    callUserCreate(data, UserLoginAlreadyExists) shouldBe None
  }

  it should "fail if user with such email already exists" in {
    //given
    val company = createRandomCompany()
    val existing = createRandomUser(company)
    val data = existing.copy(
      user = existing.user.copy(
        id = None,
        login = s"${System.nanoTime()}_rnd _login",
        version = None
      ),
      profile = existing.profile.copy(
        version = None
      )
    )

    //when & then
    callUserCreate(data, UserEmailAlreadyExists) shouldBe None
  }

  it should "create fresh new user" in {
    //given
    val company = createRandomCompany()
    val data = UserDetailsData(
      user = UserData(
        id = None,
        company = UserCompanyData(company.id.get, company.name),
        login = s" ${System.nanoTime()}_rnd _login ",
        password = s"${System.nanoTime()}_rnd _password",
        active = true
      ),
      profile = UserProfileData(
        email = s" ${System.nanoTime()}_rnd@test.com ",
        firstName = s" ${System.nanoTime()} First Name ",
        lastName = s" ${System.nanoTime()} Last Name ",
        phone = Some(s" ${System.nanoTime()} ".take(24))
      )
    )

    //when
    val result = callUserCreate(data)

    //then
    result shouldBe callUserGetById(result.user.id.get)
    
    assertUser(result, data.copy(
      user = data.user.copy(
        id = result.user.id,
        login = data.user.login.trim,
        updatedAt = result.user.updatedAt,
        createdAt = result.user.createdAt,
        version = result.user.version
      ),
      profile = data.profile.copy(
        email = data.profile.email.trim,
        firstName = data.profile.firstName.trim,
        lastName = data.profile.lastName.trim,
        phone = data.profile.phone.map(_.trim),
        updatedAt = result.profile.updatedAt,
        createdAt = result.profile.createdAt,
        version = result.profile.version
      )
    ))
    
    userDao.getById(result.user.id.get).futureValue.get.passhash shouldBe {
      HashUtils.sha1(data.user.password)
    }
  }

  "updateUser" should "fail if user doesn't exist" in {
    //given
    val company = createRandomCompany()
    val data = UserDetailsData(
      user = UserData(
        id = Some(123),
        company = UserCompanyData(company.id.get, company.name),
        login = s"${System.nanoTime()}_rnd _login",
        password = s"${System.nanoTime()}_rnd _password",
        active = true
      ),
      profile = UserProfileData(
        email = s"${System.nanoTime()}_rnd@test.com",
        firstName = s"${System.nanoTime()} First Name",
        lastName = s"${System.nanoTime()} Last Name",
        phone = Some(s"${System.nanoTime()}".take(24))
      )
    )

    //when & then
    callUserUpdate(data, UserNotFound) shouldBe None
  }

  it should "fail if company doesn't exist" in {
    //given
    val company = createRandomCompany()
    val existing = createRandomUser(company)
    val data = existing.copy(
      user = existing.user.copy(
        company = UserCompanyData(123, "Not existing")
      )
    )

    //when & then
    callUserUpdate(data, CompanyNotFound) shouldBe None

    callUserGetById(existing.user.id.get) shouldBe existing
  }

  it should "fail if user with such login already exists" in {
    //given
    val company = createRandomCompany()
    val existing = createRandomUser(company)
    val data = existing.copy(
      user = existing.user.copy(
        login = createRandomUser(company).user.login
      )
    )

    //when & then
    callUserUpdate(data, UserLoginAlreadyExists) shouldBe None

    callUserGetById(existing.user.id.get) shouldBe existing
  }

  it should "fail if user with such email already exists" in {
    //given
    val company = createRandomCompany()
    val existing = createRandomUser(company)
    val data = existing.copy(
      profile = existing.profile.copy(
        email = createRandomUser(company).profile.email
      )
    )

    //when & then
    callUserUpdate(data, UserEmailAlreadyExists) shouldBe None

    callUserGetById(existing.user.id.get) shouldBe existing
  }

  it should "fail with BadRequest if user login is blank" in {
    //given
    val company = createRandomCompany()
    val existing = createRandomUser(company)
    val data = existing.copy(
      user = existing.user.copy(
        login = " "
      )
    )

    //when & then
    callUserUpdate(data, ApiStatus(400, "login is blank")) shouldBe None

    callUserGetById(existing.user.id.get) shouldBe existing
  }

  it should "fail with BadRequest if user email is blank" in {
    //given
    val company = createRandomCompany()
    val existing = createRandomUser(company)
    val data = existing.copy(
      profile = existing.profile.copy(
        email = " "
      )
    )

    //when & then
    callUserUpdate(data, ApiStatus(400, "email is blank")) shouldBe None

    callUserGetById(existing.user.id.get) shouldBe existing
  }

  it should "fail if user already updated" in {
    //given
    val existing = createRandomUser(createRandomCompany())
    val updated = callUserUpdate(existing.copy(
      user = existing.user.copy(
        active = false
      )
    ))
    val data = updated.copy(
      user = existing.user.copy(
        active = true
      ),
      profile = updated.profile.copy(
        email = s" ${System.nanoTime()}_rnd@test.com "
      )
    )

    //when & then
    callUserUpdate(data, UserAlreadyUpdated) shouldBe None

    callUserGetById(existing.user.id.get) shouldBe updated
  }

  it should "fail if user profile already updated" in {
    //given
    val existing = createRandomUser(createRandomCompany())
    val updated = callUserUpdate(existing.copy(
      profile = existing.profile.copy(
        email = s" ${System.nanoTime()}_rnd@test.com "
      )
    ))
    val data = updated.copy(
      user = updated.user.copy(
        active = false
      ),
      profile = existing.profile.copy(
        email = s" ${System.nanoTime()}_rnd@test.com "
      )
    )

    //when & then
    callUserUpdate(data, UserAlreadyUpdated) shouldBe None

    callUserGetById(existing.user.id.get) shouldBe updated
  }

  it should "update existing user" in {
    //given
    val existing = createRandomUser(createRandomCompany())
    val company = createRandomCompany()
    val data = UserDetailsData(
      user = UserData(
        id = existing.user.id,
        company = UserCompanyData(company.id.get, company.name),
        login = s" ${System.nanoTime()}_rnd _login ",
        password = s"${System.nanoTime()}_rnd _password",
        active = false,
        version = existing.user.version
      ),
      profile = UserProfileData(
        email = s" ${System.nanoTime()}_rnd@test.com ",
        firstName = s" ${System.nanoTime()} First Name ",
        lastName = s" ${System.nanoTime()} Last Name ",
        phone = Some(s" ${System.nanoTime()} ".take(24)),
        version = existing.profile.version
      )
    )

    //when
    val result = callUserUpdate(data)

    //then
    result shouldBe callUserGetById(result.user.id.get)
    
    assertUser(result, data.copy(
      user = data.user.copy(
        id = data.user.id,
        login = data.user.login.trim,
        updatedAt = result.user.updatedAt,
        createdAt = result.user.createdAt,
        version = result.user.version
      ),
      profile = data.profile.copy(
        email = data.profile.email.trim,
        firstName = data.profile.firstName.trim,
        lastName = data.profile.lastName.trim,
        phone = data.profile.phone.map(_.trim),
        updatedAt = result.profile.updatedAt,
        createdAt = result.profile.createdAt,
        version = result.profile.version
      )
    ))

    userDao.getById(result.user.id.get).futureValue.get.passhash shouldBe {
      HashUtils.sha1(data.user.password)
    }
  }

  it should "update updated user" in {
    //given
    val existing = createRandomUser(createRandomCompany())
    val existingPasshash = userDao.getById(existing.user.id.get).futureValue.get.passhash
    val updated = callUserUpdate(existing.copy(
      user = existing.user.copy(
        active = false
      ),
      profile = existing.profile.copy(
        email = s" ${System.nanoTime()}_rnd@test.com "
      )
    ))
    val data = updated.copy(
      user = updated.user.copy(
        active = true
      ),
      profile = updated.profile.copy(
        email = s" ${System.nanoTime()}_rnd@test.com "
      )
    )

    //when
    val result = callUserUpdate(data)

    //then
    result shouldBe callUserGetById(result.user.id.get)
    
    assertUser(result, data.copy(
      user = data.user.copy(
        id = data.user.id,
        login = data.user.login.trim,
        updatedAt = result.user.updatedAt,
        createdAt = result.user.createdAt,
        version = result.user.version
      ),
      profile = data.profile.copy(
        email = data.profile.email.trim,
        firstName = data.profile.firstName.trim,
        lastName = data.profile.lastName.trim,
        phone = data.profile.phone.map(_.trim),
        updatedAt = result.profile.updatedAt,
        createdAt = result.profile.createdAt,
        version = result.profile.version
      )
    ))

    userDao.getById(result.user.id.get).futureValue.get.passhash shouldBe existingPasshash
  }

  it should "not update read-only fields" in {
    //given
    val (existing, user) = {
      val userId = createRandomUser(createRandomCompany()).user.id.get
      val user = inside(userDao.getById(userId).futureValue) {
        case Some(user) => user
      }
      userDao.update(user.copy(
        lastLoginDate = Some(new DateTime())
      )).futureValue shouldBe true
      
      (callUserGetById(userId), userDao.getById(userId).futureValue.get)
    }
    user.lastLoginDate should not be None
    
    val existingPasshash = userDao.getById(existing.user.id.get).futureValue.get.passhash
    val data = existing.copy(
      user = existing.user.copy(
        login = s" ${System.nanoTime()}_rnd _login ",
        lastLoginDate = Some(new DateTime().minusDays(1))
      ),
      profile = existing.profile.copy(
        email = s" ${System.nanoTime()}_rnd@test.com "
      )
    )

    //when
    val result = callUserUpdate(data)

    //then
    result shouldBe callUserGetById(result.user.id.get)

    assertUser(result, data.copy(
      user = data.user.copy(
        id = data.user.id,
        login = data.user.login.trim,
        lastLoginDate = user.lastLoginDate,
        updatedAt = result.user.updatedAt,
        createdAt = result.user.createdAt,
        version = result.user.version
      ),
      profile = data.profile.copy(
        email = data.profile.email.trim,
        firstName = data.profile.firstName.trim,
        lastName = data.profile.lastName.trim,
        phone = data.profile.phone.map(_.trim),
        updatedAt = result.profile.updatedAt,
        createdAt = result.profile.createdAt,
        version = result.profile.version
      )
    ))

    userDao.getById(result.user.id.get).futureValue.get.passhash shouldBe existingPasshash
  }

  it should "not update anything if data hasn't changed" in {
    //given
    val existing = createRandomUser(createRandomCompany())
    val existingPasshash = userDao.getById(existing.user.id.get).futureValue.get.passhash
    val data = existing

    //when
    val result = callUserUpdate(data)

    //then
    result shouldBe callUserGetById(existing.user.id.get)
    result shouldBe existing

    userDao.getById(result.user.id.get).futureValue.get.passhash shouldBe existingPasshash
  }

  it should "update only user data" in {
    //given
    val existing = createRandomUser(createRandomCompany())
    val data = existing.copy(
      user = existing.user.copy(
        active = false
      )
    )

    //when
    val result = callUserUpdate(data)

    //then
    result shouldBe callUserGetById(existing.user.id.get)

    result.user.active shouldBe data.user.active
    result.user.version.get shouldBe (existing.user.version.get + 1)
    
    result.profile shouldBe existing.profile
  }

  it should "update only user profile data" in {
    //given
    val existing = createRandomUser(createRandomCompany())
    val data = existing.copy(
      profile = existing.profile.copy(
        email = s"${System.nanoTime()}_rnd@test.com"
      )
    )

    //when
    val result = callUserUpdate(data)

    //then
    result shouldBe callUserGetById(existing.user.id.get)

    result.user shouldBe existing.user
    
    result.profile.email shouldBe data.profile.email
    result.profile.version.get shouldBe (existing.profile.version.get + 1)
  }

  private def assertUser(result: UserDetailsData, expected: UserDetailsData): Unit = {
    inside (result) {
      case UserDetailsData(user, profile) =>
        inside(user) {
          case UserData(
          id,
          company,
          login,
          password,
          active,
          lastLoginDate,
          updatedAt,
          createdAt,
          version
          ) =>
            id shouldBe expected.user.id
            company shouldBe expected.user.company
            login shouldBe expected.user.login
            password shouldBe "*****"
            active shouldBe expected.user.active
            lastLoginDate.map(_.toDateTime(DateTimeZone.UTC)) shouldBe {
              expected.user.lastLoginDate.map(_.toDateTime(DateTimeZone.UTC))
            }
            updatedAt.get.getMillis should be > 0L
            createdAt.get.getMillis should be <= updatedAt.get.getMillis
            version shouldBe expected.user.version
        }
        inside(profile) {
          case UserProfileData(email, firstName, lastName, phone, updatedAt, createdAt, version) =>
            email shouldBe expected.profile.email
            firstName shouldBe expected.profile.firstName
            lastName shouldBe expected.profile.lastName
            phone shouldBe expected.profile.phone
            updatedAt.get.getMillis should be > 0L
            createdAt.get.getMillis should be <= updatedAt.get.getMillis
            version shouldBe expected.profile.version
        }
    }
  }
}
