package scommons.service.test.it.docker

import com.typesafe.config.{Config, ConfigFactory}
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.collection.JavaConverters._
import scala.concurrent.duration._

trait DockerIntegrationTestSuite extends TestSuite
  with ScalaFutures
  with DockerTestKit
  with DockerKitSpotify {

  override val StopContainersTimeout: FiniteDuration = 1.minute

  override val StartContainersTimeout: FiniteDuration = 1.minute

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(1.minute, 1.second)

  def configure(config: Config, overrideWithConf: (String, Any)*): Config = {
    def toJava(data: Any): Any = data match {
      case map: Map[_, _] => map.mapValues(toJava).asJava
      case iterable: Iterable[_] => iterable.map(toJava).asJava
      case v => v
    }

    ConfigFactory.parseMap(toJava(overrideWithConf.toMap).asInstanceOf[java.util.Map[String, AnyRef]])
      .withFallback(config)
  }

  override def beforeAll(): Unit = {
    super.beforeAll()

    dockerContainers.map(_.image).foreach(println)
    if (!dockerContainers.forall(_.isReady().futureValue)) {
      throw new IllegalStateException("Not all dockerContainers are up!")
    }
  }
}
