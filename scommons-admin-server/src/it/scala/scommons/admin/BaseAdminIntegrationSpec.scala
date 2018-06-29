package scommons.admin

import java.util.UUID

import org.scalatest._
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.ConfiguredServer
import scommons.admin.client.api.AdminUiApiClient
import scommons.admin.client.api.company.CompanyData
import scommons.admin.domain.dao.CompanyDao
import scommons.api.ApiStatus
import services.CompanyService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

trait BaseAdminIntegrationSpec extends FlatSpec
  with Matchers
  with ConfiguredServer
  with ScalaFutures
  with Inside
  with Eventually
  with BeforeAndAfterEach {

  implicit val defaultPatience: PatienceConfig = PatienceConfig(
    timeout = Span(5, Seconds),
    interval = Span(100, Millis)
  )

  private def inject[T: ClassTag]: T = app.injector.instanceOf[T]

  protected lazy val companyService: CompanyService = inject[CompanyService]
  protected lazy val companyDao: CompanyDao = inject[CompanyDao]
  
  private lazy val uiApiClient = inject[AdminUiApiClient]

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
}
