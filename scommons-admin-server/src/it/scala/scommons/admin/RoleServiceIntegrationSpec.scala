package scommons.admin

import org.scalatest.DoNotDiscover
import scommons.admin.domain.Role

@DoNotDiscover
class RoleServiceIntegrationSpec extends BaseAdminIntegrationSpec {

  it should "fail if role bit_index limit is reached when create role" in {
    //given
    val group = createRandomSystemGroup()
    val system = createRandomSystem(group.id.get)
    for (_ <- 1 to 64) {
      createRandomRole(system.id.get)
    }
    val entity = Role(
      id = -1,
      systemId = system.id.get,
      title = s"${System.nanoTime()}",
      bitIndex = 0,
      updatedBy = superUserId
    )

    //when
    val futureResult = roleService.createRole(entity)

    //then
    val e = futureResult.failed.futureValue
    e.getMessage should include("Reached role bit_index limit(63)")
  }
}
