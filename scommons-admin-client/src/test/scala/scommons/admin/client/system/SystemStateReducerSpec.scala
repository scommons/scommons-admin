package scommons.admin.client.system

import scommons.admin.client.api.system._
import scommons.admin.client.system.SystemActions._
import scommons.react.test.TestSpec

class SystemStateReducerSpec extends TestSpec {

  private val reduce = SystemStateReducer.apply _
  
  it should "return empty list if no such parentId when state.getSystems" in {
    //when & then
    SystemState().getSystems(123) shouldBe Nil
  }
  
  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe SystemState()
  }
  
  it should "set showCreatePopup when SystemCreateRequestAction" in {
    //when & then
    reduce(Some(SystemState()), SystemCreateRequestAction(true)) shouldBe {
      SystemState(showCreatePopup = true)
    }
    reduce(Some(SystemState(showCreatePopup = true)), SystemCreateRequestAction(false)) shouldBe {
      SystemState()
    }
  }
  
  it should "set showEditPopup when SystemUpdateRequestAction" in {
    //when & then
    reduce(Some(SystemState()), SystemUpdateRequestAction(true)) shouldBe {
      SystemState(showEditPopup = true)
    }
    reduce(Some(SystemState(showEditPopup = true)), SystemUpdateRequestAction(false)) shouldBe {
      SystemState()
    }
  }
  
  it should "set data in state when SystemListFetchedAction" in {
    //given
    val dataList = List(
      SystemData(
        id = Some(11),
        name = "test name",
        password = "test password",
        title = "test title",
        url = "http://test.com",
        parentId = 1
      ),
      SystemData(
        id = Some(12),
        name = "test name",
        password = "test password",
        title = "test title",
        url = "http://test.com",
        parentId = 1
      ),
      SystemData(
        id = Some(21),
        name = "test name",
        password = "test password",
        title = "test title",
        url = "http://test.com",
        parentId = 2
      )
    )
    
    //when & then
    reduce(Some(SystemState()), SystemListFetchedAction(dataList)) shouldBe {
      SystemState(dataList.groupBy(_.parentId))
    }
  }

  it should "append new data to the state when SystemCreatedAction" in {
    //given
    val dataList = List(SystemData(
      id = Some(11),
      name = "test name",
      password = "test password",
      title = "test title",
      url = "http://test.com",
      parentId = 1
    ))
    val data = SystemData(
      id = Some(12),
      name = "test name 2",
      password = "test password 2",
      title = "test title 2",
      url = "http://test2.com",
      parentId = 1
    )

    //when & then
    reduce(Some(SystemState(
      dataList.groupBy(_.parentId),
      showCreatePopup = true
    )), SystemCreatedAction(data)) shouldBe {
      SystemState((dataList :+ data).groupBy(_.parentId))
    }
  }
  
  it should "update state when SystemUpdatedAction" in {
    //given
    val existingData = SystemData(
      id = Some(12),
      name = "test name 2",
      password = "test password 2",
      title = "test title 2",
      url = "http://test2.com",
      parentId = 1
    )
    val dataList = List(
      SystemData(
        id = Some(11),
        name = "test name",
        password = "test password",
        title = "test title",
        url = "http://test.com",
        parentId = 1
      ),
      existingData
    )
    val data = SystemData(
      id = Some(11),
      name = "updated test name",
      password = "updated test password",
      title = "updated test title",
      url = "http://updated.test.com",
      parentId = 1
    )

    //when & then
    reduce(Some(SystemState(
      dataList.groupBy(_.parentId),
      showEditPopup = true
    )), SystemUpdatedAction(data)) shouldBe {
      SystemState(List(
        data,
        existingData
      ).groupBy(_.parentId))
    }
  }
}
