package controllers

import akka.testkit.SocketUtil
import com.ticketfly.play.liquibase.PlayLiquibaseModule
import modules.ApplicationModule
import org.scalatest.{Suites, TestSuite}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import scaldi.play.ScaldiApplicationBuilder

class ControllersSpec extends Suites(
  new AdminControllerSpec,
  new SwaggerControllerSpec
) with TestSuite
  with GuiceOneServerPerSuite {

  override lazy val port: Int = {
    val (_, serverPort) = SocketUtil.temporaryServerHostnameAndPort()
    serverPort
  }

  implicit override lazy val app: Application = new ScaldiApplicationBuilder(
    disabled = List(
      classOf[ApplicationModule],
      classOf[PlayLiquibaseModule]
    )
  ).build()
}
