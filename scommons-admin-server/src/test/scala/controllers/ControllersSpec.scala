package controllers

import akka.actor.ActorSystem
import akka.testkit.SocketUtil
import com.ticketfly.play.liquibase.PlayLiquibaseModule
import controllers.ui.{CompanyController, SystemGroupController}
import modules.ApplicationModule
import org.scalatest.{Suites, TestSuite}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.mvc.ControllerComponents
import scaldi.Module
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
    ),
    modules = List(new Module {
      private implicit lazy val ec = inject[ActorSystem].dispatcher
      private implicit lazy val components = inject[ControllerComponents]
      
      //test-only
      bind[CompanyController] to new CompanyController(null)
      bind[SystemGroupController] to new SystemGroupController(null)
    })
  ).build()
}
