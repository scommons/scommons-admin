package scommons.admin

import java.text.Collator
import java.util.{Locale, UUID}

import org.scalatest.DoNotDiscover
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.company.CompanyData
import scommons.api.ApiStatus

@DoNotDiscover
class CompanyApiIntegrationSpec extends BaseAdminIntegrationSpec {

  "getCompanyById" should "fail if no such company" in {
    //when & then
    callCompanyGetById(12345, CompanyNotFound) shouldBe None
  }

  "listCompanies" should "return paginated, filtered list, ordered by name" in {
    //given
    //removeAllCompanies()
    val collator = Collator.getInstance(Locale.US)
    implicit val o: Ordering[String] = new Ordering[String] {
      def compare(x: String, y: String): Int = collator.compare(x, y)
    }

    val symbols = s"${System.nanoTime()}SeArCH"
    val companies = List(
      CompanyData(Some(1), "Test Company"),
      createRandomCompany(),
      createRandomCompany(partOfName = Some(symbols.toUpperCase)),
      createRandomCompany(partOfName = Some(symbols)),
      createRandomCompany(partOfName = Some(symbols.toLowerCase)),
      createRandomCompany(partOfName = Some(symbols))
    ).sortBy(_.name)

    def listAndAssert(offset: Option[Int],
                      limit: Option[Int],
                      partOfName: Option[String]): Unit = {

      callCompanyList(offset, limit, partOfName) shouldBe {
        val filteredList = companies
          .filter(_.name.toLowerCase.contains(partOfName.map(_.toLowerCase).getOrElse("")))
        val list = filteredList
          .slice(offset.getOrElse(0), offset.getOrElse(0) + limit.getOrElse(10))
        
        (list, if (offset.isDefined) None else Some(filteredList.size))
      }
    }

    //when & then
    listAndAssert(offset = None, limit = None, partOfName = None)
    listAndAssert(offset = None, limit = Some(1), partOfName = None)
    listAndAssert(offset = None, limit = Some(2), partOfName = Some(symbols))
    listAndAssert(offset = Some(1), limit = None, partOfName = None)
    listAndAssert(offset = Some(2), limit = None, partOfName = Some(symbols))
    listAndAssert(offset = Some(1), limit = Some(2), partOfName = None)
    listAndAssert(offset = Some(2), limit = Some(2), partOfName = Some(symbols))
  }

  "createCompany" should "fail if company with such name already exists" in {
    //given
    val existing = createRandomCompany()
    val data = CompanyData(
      None,
      existing.name
    )

    //when & then
    callCompanyCreate(data, CompanyAlreadyExists) shouldBe None
  }

  it should "create fresh new company" in {
    //given
    val data = CompanyData(
      None,
      s"  ${UUID.randomUUID()}  "
    )

    //when
    val result = callCompanyCreate(data)

    //then
    result.id should not be None
    result.name shouldBe data.name.trim

    assertCompany(result, callCompanyGetById(result.id.get))
  }

  "updateCompany" should "fail if company doesn't exist" in {
    //given
    val data = CompanyData(
      Some(12345),
      s"${UUID.randomUUID()}"
    )

    //when & then
    callCompanyUpdate(data, CompanyNotFound) shouldBe None
  }

  it should "fail if company with such name already exists" in {
    //given
    val existing = createRandomCompany()
    val data = CompanyData(
      createRandomCompany().id,
      existing.name
    )

    //when & then
    callCompanyUpdate(data, CompanyAlreadyExists) shouldBe None
  }

  it should "fail with BadRequest if company name is blank" in {
    //given
    val existing = createRandomCompany()
    val data = CompanyData(
      existing.id,
      " "
    )

    //when & then
    callCompanyUpdate(data, ApiStatus(400, "name is blank")) shouldBe None
  }

  it should "update existing company" in {
    //given
    val existing = createRandomCompany()
    val data = CompanyData(
      existing.id,
      s"  ${UUID.randomUUID()}  "
    )

    //when
    val result = callCompanyUpdate(data)

    //then
    result.id shouldBe existing.id
    result.name shouldBe data.name.trim

    assertCompany(result, callCompanyGetById(result.id.get))
  }

  private def assertCompany(result: CompanyData, expected: CompanyData): Unit = {
    inside (result) {
      case CompanyData(id, name) =>
        id shouldBe expected.id
        name shouldBe expected.name
    }
  }
}
