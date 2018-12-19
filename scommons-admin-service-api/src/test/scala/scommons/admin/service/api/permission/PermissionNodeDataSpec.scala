package scommons.admin.service.api.permission

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json._

class PermissionNodeDataSpec extends FlatSpec
  with Matchers {

  private val data = PermissionNodeData(
    name = "admin",
    title = "Admin",
    permissions = List(
      PermissionData(name = "perm1", title = "Perm1", roles = Some(List(1L))),
      PermissionData(name = "perm2", title = "Perm2", roles = Some(List(2L)))
    ),
    nodes = Some(List(PermissionNodeData(
      name = "subNode",
      title = "SubNode",
      permissions = List(
        PermissionData(name = "perm3", title = "Perm3")
      )
    )))
  )

  private val json =
    s"""{
       |  "name" : "admin",
       |  "title" : "Admin",
       |  "permissions" : [ {
       |    "name" : "perm1",
       |    "title" : "Perm1",
       |    "roles" : [ 1 ]
       |  }, {
       |    "name" : "perm2",
       |    "title" : "Perm2",
       |    "roles" : [ 2 ]
       |  } ],
       |  "nodes" : [ {
       |    "name" : "subNode",
       |    "title" : "SubNode",
       |    "permissions" : [ {
       |      "name" : "perm3",
       |      "title" : "Perm3"
       |    } ]
       |  } ]
       |}""".stripMargin

  it should "serialize to json" in {
    //when & then
    Json.prettyPrint(Json.toJson(data)) shouldBe json
  }

  it should "deserialize from json" in {
    //when & then
    Json.parse(json).as[PermissionNodeData] shouldBe data
  }
}
