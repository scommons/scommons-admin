package scommons.admin

import akka.actor.ActorSystem
import akka.testkit.SocketUtil
import org.scalatest.{Suites, TestSuite}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
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
  new UserApiIntegrationSpec
) with TestSuite
  with MockitoSugar
  with GuiceOneServerPerSuite
  with DockerIntegrationTestSuite
  with DockerPostgresService {

  override lazy val port: Int = {
    val (_, serverPort) = SocketUtil.temporaryServerHostnameAndPort()
    serverPort
  }
  
  private val dbAdminUser = "admin_admin"
  private val dbAdminPass = "superadmin"
  private val dbName = "admin_db"

  implicit override lazy val app: Application = {
    val adminUiApiUrl = s"http://localhost:$port/scommons-admin/ui"
    println(s"adminUiApiUrl: $adminUiApiUrl")

    val uiApiClient = new AdminUiApiClient(
      new WsApiHttpClient(adminUiApiUrl)(ActorSystem("AdminUiApiWsClient"))
    )

    new ScaldiApplicationBuilder(modules = List(new Module {
      //test-only
      bind[AdminUiApiClient] to uiApiClient
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
}
