package scommons.admin

import org.scalatest._
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.ConfiguredServer

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

  //private def inject[T: ClassTag]: T = app.injector.instanceOf[T]

  //private lazy val apiClient = inject[AdminApiClient]

  ////////////////////////////////////////////////////////////////////////////////////////
  // companies

}
