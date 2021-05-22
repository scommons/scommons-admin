package scommons.admin.client.user

import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions._
import scommons.admin.client.user.UserActionsSpec.UserActionsTest
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class UserActionsSpec extends AsyncTestSpec {

  it should "dispatch UserListFetchedAction when userListFetch" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val offset = Some(12)
    val symbols = Some("test")
    val dataList = List(mock[UserData])
    val totalCount = Some(12345)
    val expectedResp = UserListResp(dataList, totalCount)

    (api.listUsers _).expects(offset, Some(UserActions.listLimit), symbols)
      .returning(Future.successful(expectedResp))
    dispatch.expects(UserListFetchedAction(dataList, totalCount))
    
    //when
    val UserListFetchAction(FutureTask(message, future), resultOffset) =
      actions.userListFetch(dispatch, offset, symbols)
    
    //then
    resultOffset shouldBe offset
    message shouldBe "Fetching Users"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch UserFetchedAction when userFetch" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val id = 1
    val respData = mock[UserDetailsData]
    val expectedResp = UserDetailsResp(respData)

    (api.getUserById _).expects(id)
      .returning(Future.successful(expectedResp))
    dispatch.expects(UserFetchedAction(respData))
    
    //when
    val UserFetchAction(FutureTask(message, future)) =
      actions.userFetch(dispatch, id)
    
    //then
    message shouldBe "Fetching User"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch UserCreatedAction when userCreate" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val data = mock[UserDetailsData]
    val respData = mock[UserDetailsData]
    val expectedResp = UserDetailsResp(respData)

    (api.createUser _).expects(data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(UserCreatedAction(expectedResp.data.get))
    
    //when
    val UserCreateAction(FutureTask(message, future)) =
      actions.userCreate(dispatch, data)
    
    //then
    message shouldBe "Creating User"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch UserDetailsUpdatedAction when userUpdate" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val data = mock[UserDetailsData]
    val respData = mock[UserDetailsData]
    val expectedResp = UserDetailsResp(respData)

    (api.updateUser _).expects(data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(UserDetailsUpdatedAction(respData))
    
    //when
    val UserUpdateAction(FutureTask(message, future)) =
      actions.userUpdate(dispatch, data)
    
    //then
    message shouldBe "Updating User"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object UserActionsSpec {
  
  private class UserActionsTest(api: UserApi)
    extends UserActions {

    protected def client: UserApi = api
  }
}
