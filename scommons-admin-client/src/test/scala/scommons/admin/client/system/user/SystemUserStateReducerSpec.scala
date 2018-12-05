package scommons.admin.client.system.user

import scommons.admin.client.api.system.user._
import scommons.admin.client.system.user.SystemUserActions._
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec

import scala.concurrent.Future

class SystemUserStateReducerSpec extends TestSpec {

  private val reduce = SystemUserStateReducer.apply _
  
  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe SystemUserState()
  }
  
  it should "set systemId and offset when SystemUserListFetchAction" in {
    //given
    val task = FutureTask("test task", Future.successful(SystemUserListResp(Nil, None)))
    val systemId = 12
    val offset = Some(123)
    
    //when & then
    reduce(Some(SystemUserState()), SystemUserListFetchAction(task, systemId, offset)) shouldBe SystemUserState(
      systemId = Some(systemId),
      offset = offset
    )
  }
  
  it should "set dataList and totalCount when SystemUserListFetchedAction" in {
    //given
    val data = mock[SystemUserData]
    val dataList = List(data)
    val totalCount = Some(123)
    
    //when & then
    reduce(Some(SystemUserState()), SystemUserListFetchedAction(dataList, totalCount)) shouldBe SystemUserState(
      dataList = dataList,
      totalCount = totalCount
    )

    //when & then
    reduce(Some(SystemUserState(
      totalCount = totalCount
    )), SystemUserListFetchedAction(dataList, None)) shouldBe SystemUserState(
      dataList = dataList,
      totalCount = totalCount
    )
  }
}
