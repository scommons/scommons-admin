package scommons.admin.server

import akka.actor.ActorSystem
import akka.stream.Materializer
import org.scalatest.{Suites, TestSuite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.{Application, OptionalDevContext}
import scaldi.Module
import scaldi.play.ScaldiApplicationBuilder
import scommons.admin.client.api.AdminUiApiClient
import scommons.api.http.ws.WsApiHttpClient
import scommons.service.test.it.docker._

class AllAdminIntegrationTests extends Suites(
  new CompanyApiIntegrationSpec,
  new SystemGroupApiIntegrationSpec,
  new SystemApiIntegrationSpec,
  new RoleApiIntegrationSpec,
  new RoleServiceIntegrationSpec,
  new RolePermissionApiIntegrationSpec,
  new UserApiIntegrationSpec,
  new UserSystemApiIntegrationSpec,
  new SystemUserApiIntegrationSpec
) with TestSuite
  with MockitoSugar
  with GuiceOneServerPerSuite
  with DockerIntegrationTestSuite
  with DockerPostgresService {

  private val dbAdminUser = "admin_admin"
  private val dbAdminPass = "superadmin"
  private val dbName = "admin_db"

  private var wsClient: StandaloneAhcWSClient = _

  implicit override lazy val app: Application = {

    def uiApiClient(implicit system: ActorSystem) = {
      implicit val materializer: Materializer = Materializer(system)

      val adminUiApiUrl = s"http://localhost:$port/scommons-admin/ui"
      println(s"adminUiApiUrl: $adminUiApiUrl")

      wsClient = StandaloneAhcWSClient()
      new AdminUiApiClient(new WsApiHttpClient(wsClient, adminUiApiUrl)(system.dispatcher))
    }

    new ScaldiApplicationBuilder(modules = List(new Module {
      bind[OptionalDevContext] to new OptionalDevContext(None)
      //test-only
      bind[AdminUiApiClient] to uiApiClient(
        inject[ActorSystem]
      )
    })).configure(
      "quill.db.host" -> "localhost",
      "quill.db.port" -> postgresPort,
      "quill.db.user" -> "admin",
      "quill.db.password" -> "admin",
      "liquibase.url" -> s"jdbc:postgresql://localhost:$postgresPort/$dbName",
      "liquibase.user" -> dbAdminUser,
      "liquibase.password" -> dbAdminPass
    ).build()
  }

  override def beforeAll(): Unit = {
    super.beforeAll()

    initializeDb("/scommons/admin/dao/changelog/createDb.sql")
    initializeDb("/scommons/admin/dao/changelog/initialSql.sql", dbAdminUser, dbAdminPass, dbName)
    initializeDb("/test_data.sql", dbAdminUser, dbAdminPass, dbName)
  }

  override def afterAll(): Unit = {
    wsClient.close()

    super.afterAll()
  }
}
