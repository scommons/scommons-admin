package scommons.admin

import akka.actor.ActorSystem
import akka.testkit.SocketUtil
import org.scalatest.{Suites, TestSuite}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import scaldi.Module
import scaldi.play.ScaldiApplicationBuilder
import scommons.admin.client.api.AdminApiClient
import scommons.api.http.ws.WsApiHttpClient
import scommons.service.test.it.docker._

class AllAdminIntegrationTests extends Suites(
  new CompanyApiIntegrationSpec
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
    val adminApiUrl = s"http://localhost:$port/scommons-admin"
    println(s"adminApiUrl: $adminApiUrl")

    val apiClient = new AdminApiClient(new WsApiHttpClient(adminApiUrl)(ActorSystem("AdminApiWsClient")))

    new ScaldiApplicationBuilder(modules = List(new Module {
      //test-only
      bind[AdminApiClient] to apiClient
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
    if (true) {
      
    }
  }
}
