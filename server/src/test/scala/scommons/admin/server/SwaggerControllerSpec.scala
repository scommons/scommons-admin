package scommons.admin.server

import org.scalatest.DoNotDiscover

@DoNotDiscover
class SwaggerControllerSpec extends BaseControllersSpec {

  ignore should "return api-docs json" in {
    //given
    val request = wsUrl(s"$baseUrl/api-docs")

    //when
    val response = request.get.futureValue

    //then
    response.status shouldBe 200
    response.contentType shouldBe "application/json"

    val json = response.json
    (json \ "swagger").as[String] shouldBe "2.0"
    (json \ "info" \ "description").as[String] shouldBe "REST API for scommons Admin Server"
    (json \ "info" \ "version").as[String] shouldBe "1.0"
    (json \ "basePath").as[String] shouldBe baseUrl
  }

  it should "redirect to swagger ui html page when /swagger.html" in {
    //given
    val request = wsUrl(s"$baseUrl/swagger.html")

    //when
    val response = request.get.futureValue

    //then
    response.status shouldBe 200
    response.contentType shouldBe "text/html; charset=UTF-8"
    response.body should include ("<title>Swagger UI</title>")
  }

  it should "return swagger ui html page from web-jar assets" in {
    //given
    val request = wsUrl(s"$baseUrl/assets/lib/swagger-ui/index.html")

    //when
    val response = request.get.futureValue

    //then
    response.status shouldBe 200
    response.contentType shouldBe "text/html; charset=UTF-8"
    response.body should include ("<title>Swagger UI</title>")
  }
}
