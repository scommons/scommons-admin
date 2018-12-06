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
  
  it should "set params when SystemUserParamsChangedAction" in {
    //given
    val params = SystemUserParams(Some(1), Some(2))

    //when & then
    reduce(Some(SystemUserState()), SystemUserParamsChangedAction(params)) shouldBe {
      SystemUserState(params = params)
    }
  }
  
  it should "set offset when SystemUserListFetchAction" in {
    //given
    val task = FutureTask("test task", Future.successful(SystemUserListResp(Nil, None)))
    val offset = Some(123)
    
    //when & then
    reduce(Some(SystemUserState()), SystemUserListFetchAction(task, offset)) shouldBe SystemUserState(
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
