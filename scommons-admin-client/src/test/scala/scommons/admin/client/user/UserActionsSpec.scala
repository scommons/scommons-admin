package scommons.admin.client.user

import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions._
import scommons.admin.client.user.UserActionsSpec.UserActionsTest
import scommons.client.task.FutureTask
import scommons.client.test.AsyncTestSpec

import scala.concurrent.Future

class UserActionsSpec extends AsyncTestSpec {

  it should "dispatch UserListFetchedAction when userListFetch" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val offset = Some(12)
    val symbols = Some("test")
    val dataList = List(UserData(
      id = None,
      company = UserCompanyData(1, "Test Company"),
      login = "test_login",
      password = "test",
      active = true
    ))
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
  
  it should "dispatch UserCreatedAction when userCreate" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val data = UserDetailsData(
      user = UserData(
        id = None,
        company = UserCompanyData(1, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      ),
      profile = UserProfileData(
        email = "test@email.com",
        firstName = "Firstname",
        lastName = "Lastname",
        phone = Some("0123 456 789")
      )
    )
    val expectedResp = UserDetailsResp(data.copy(user = data.user.copy(id = Some(1))))

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
  
  it should "dispatch UserUpdatedAction when userUpdate" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val data = UserDetailsData(
      user = UserData(
        id = Some(1),
        company = UserCompanyData(1, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      ),
      profile = UserProfileData(
        email = "test@email.com",
        firstName = "Firstname",
        lastName = "Lastname",
        phone = Some("0123 456 789")
      )
    )
    val respData = data.copy(user = data.user.copy(login = "updated_login"))
    val expectedResp = UserDetailsResp(respData)

    (api.updateUser _).expects(data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(UserUpdatedAction(respData))
    
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
