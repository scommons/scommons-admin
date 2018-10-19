package scommons.admin.server

import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Inside, Matchers}
import org.scalatestplus.play.{ConfiguredServer, WsScalaTestClient}
import play.api.libs.ws.WSClient

import scala.reflect.ClassTag

trait BaseControllersSpec extends FlatSpec
  with Matchers
  with ConfiguredServer
  with ScalaFutures
  with Inside
  with Eventually
  with BeforeAndAfterEach
  with WsScalaTestClient {

  implicit val defaultPatience: PatienceConfig = PatienceConfig(
    timeout = Span(5, Seconds),
    interval = Span(100, Millis)
  )

  private def inject[T: ClassTag]: T = app.injector.instanceOf[T]

  implicit lazy val wsClient: WSClient = inject[WSClient]

  protected lazy val baseUrl = "/scommons-admin"
}
