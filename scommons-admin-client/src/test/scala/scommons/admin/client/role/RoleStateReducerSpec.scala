package scommons.admin.client.role

import scommons.admin.client.api.role._
import scommons.admin.client.role.RoleActions._
import scommons.client.test.TestSpec

class RoleStateReducerSpec extends TestSpec {

  private val reduce = RoleStateReducer.apply _
  
  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe RoleState()
  }
  
  it should "set showCreatePopup when RoleCreateRequestAction" in {
    //when & then
    reduce(Some(RoleState()), RoleCreateRequestAction(true)) shouldBe {
      RoleState(showCreatePopup = true)
    }
    reduce(Some(RoleState(showCreatePopup = true)), RoleCreateRequestAction(false)) shouldBe {
      RoleState()
    }
  }
  
  it should "set showEditPopup when RoleUpdateRequestAction" in {
    //when & then
    reduce(Some(RoleState()), RoleUpdateRequestAction(true)) shouldBe {
      RoleState(showEditPopup = true)
    }
    reduce(Some(RoleState(showEditPopup = true)), RoleUpdateRequestAction(false)) shouldBe {
      RoleState()
    }
  }
  
  it should "set data in state when RoleListFetchedAction" in {
    //given
    val dataList = List(RoleData(Some(1), 3, "test title"))
    
    //when & then
    reduce(Some(RoleState()), RoleListFetchedAction(dataList)) shouldBe {
      RoleState(dataList.groupBy(_.systemId))
    }
  }

  it should "append new data to the state when RoleCreatedAction" in {
    //given
    val dataList = List(RoleData(Some(1), 3, "test title"))
    val data = RoleData(Some(2), 3, "test title 2")

    //when & then
    reduce(Some(RoleState(dataList.groupBy(_.systemId))), RoleCreatedAction(data)) shouldBe {
      RoleState((dataList :+ data).groupBy(_.systemId))
    }
  }
  
  it should "update state when RoleUpdatedAction" in {
    //given
    val existingData = RoleData(Some(2), 3, "test title 2")
    val dataList = List(
      RoleData(Some(1), 3, "test title"),
      existingData
    )
    val data = RoleData(Some(1), 3, "updated test title")

    //when & then
    reduce(Some(RoleState(dataList.groupBy(_.systemId))), RoleUpdatedAction(data)) shouldBe {
      RoleState(List(
        data,
        existingData
      ).groupBy(_.systemId))
    }
  }
}
