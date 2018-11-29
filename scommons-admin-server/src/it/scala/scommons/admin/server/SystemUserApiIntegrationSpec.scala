package scommons.admin.server

import java.text.Collator
import java.util.Locale

import org.joda.time.DateTimeZone
import org.scalatest.DoNotDiscover
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.system.user.SystemUserData
import scommons.admin.client.api.user.system.UserSystemUpdateReq

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

      val systemUsers = systemUserDao.getBySystemId(systemId).futureValue
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
}
