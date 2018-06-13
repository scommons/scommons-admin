package scommons.admin

import akka.actor.ActorSystem
import akka.testkit.SocketUtil
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Suites, TestSuite}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import scaldi.Module
import scaldi.play.ScaldiApplicationBuilder
import scommons.admin.client.api.AdminApiClient
import scommons.api.http.ws.WsApiHttpClient

class AllAdminIntegrationTests extends Suites(
  new CompanyApiIntegrationSpec
) with TestSuite
  with MockitoSugar
  with GuiceOneServerPerSuite {

  override lazy val port: Int = {
    val (_, serverPort) = SocketUtil.temporaryServerHostnameAndPort()
    serverPort
  }

  implicit override lazy val app: Application = {
    val adminApiUrl = s"http://localhost:$port/scommons-admin"
    println(s"adminApiUrl: $adminApiUrl")

    val apiClient = new AdminApiClient(new WsApiHttpClient(adminApiUrl)(ActorSystem("AdminApiWsClient")))

    new ScaldiApplicationBuilder(modules = List(new Module {
      //test-only
      bind[AdminApiClient] to apiClient
    })).configure(
      // custom configuration
      //"quill.db.port" -> postgresPort
    ).build()
  }
}
