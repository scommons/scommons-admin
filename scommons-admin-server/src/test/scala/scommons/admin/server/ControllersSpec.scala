package scommons.admin.server

import akka.actor.ActorSystem
import akka.testkit.SocketUtil
import com.ticketfly.play.liquibase.PlayLiquibaseModule
import org.scalatest.{Suites, TestSuite}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.mvc.ControllerComponents
import scaldi.Module
import scaldi.play.ScaldiApplicationBuilder
import scommons.admin.server.company.CompanyController
import scommons.admin.server.role.RoleController
import scommons.admin.server.role.permission.RolePermissionController
import scommons.admin.server.system.SystemController
import scommons.admin.server.system.group.SystemGroupController
import scommons.admin.server.user.UserController
import scommons.admin.server.user.system.UserSystemController

import scala.concurrent.ExecutionContext

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
      classOf[AdminModule],
      classOf[PlayLiquibaseModule]
    ),
    modules = List(new Module {
      private implicit lazy val ec: ExecutionContext = inject[ActorSystem].dispatcher
      private implicit lazy val components: ControllerComponents = inject[ControllerComponents]
      
      //test-only
      bind[CompanyController] to new CompanyController(null)
      bind[SystemGroupController] to new SystemGroupController(null)
      bind[SystemController] to new SystemController(null)
      bind[RoleController] to new RoleController(null)
      bind[RolePermissionController] to new RolePermissionController(null)
      bind[UserController] to new UserController(null)
      bind[UserSystemController] to new UserSystemController(null)
    })
  ).build()
}
