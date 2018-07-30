package scommons.admin.client.system.group

import scommons.admin.client.api.system.group._
import scommons.admin.client.system.group.action._
import scommons.client.test.TestSpec

class SystemGroupStateReducerSpec extends TestSpec {

  private val reduce = SystemGroupStateReducer.apply _
  
  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe SystemGroupState()
  }
  
  it should "set showCreatePopup when SystemGroupCreateRequestAction" in {
    //when & then
    reduce(Some(SystemGroupState()), SystemGroupCreateRequestAction(true)) shouldBe {
      SystemGroupState(showCreatePopup = true)
    }
    reduce(Some(SystemGroupState(showCreatePopup = true)), SystemGroupCreateRequestAction(false)) shouldBe {
      SystemGroupState()
    }
  }
  
  it should "set showEditPopup when SystemGroupUpdateRequestAction" in {
    //when & then
    reduce(Some(SystemGroupState()), SystemGroupUpdateRequestAction(true)) shouldBe {
      SystemGroupState(showEditPopup = true)
    }
    reduce(Some(SystemGroupState(showEditPopup = true)), SystemGroupUpdateRequestAction(false)) shouldBe {
      SystemGroupState()
    }
  }
  
  it should "set dataList when SystemGroupListFetchedAction" in {
    //given
    val dataList = List(SystemGroupData(Some(1), "test name"))
    
    //when & then
    reduce(Some(SystemGroupState()), SystemGroupListFetchedAction(dataList)) shouldBe {
      SystemGroupState(dataList)
    }
  }

  it should "append new data to the dataList when SystemGroupCreatedAction" in {
    //given
    val dataList = List(SystemGroupData(Some(1), "test name"))
    val data = SystemGroupData(Some(2), "test name 2")

    //when & then
    reduce(Some(SystemGroupState(dataList)), SystemGroupCreatedAction(data)) shouldBe {
      SystemGroupState(dataList :+ data)
    }
  }
  
  it should "update dataList when SystemGroupUpdatedAction" in {
    //given
    val existingData = SystemGroupData(Some(2), "test name 2")
    val dataList = List(
      SystemGroupData(Some(1), "test name"),
      existingData
    )
    val data = SystemGroupData(Some(1), "updated test name")

    //when & then
    reduce(Some(SystemGroupState(dataList)), SystemGroupUpdatedAction(data)) shouldBe {
      SystemGroupState(List(
        data,
        existingData
      ))
    }
  }
}
