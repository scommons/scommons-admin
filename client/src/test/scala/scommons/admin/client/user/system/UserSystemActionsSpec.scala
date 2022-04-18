package scommons.admin.client.user.system

import scommons.admin.client.api.user.system._
import scommons.admin.client.api.user.{UserCompanyData, UserData}
import scommons.admin.client.user.UserActions.UserUpdatedAction
import scommons.admin.client.user.system.UserSystemActions._
import scommons.admin.client.user.system.UserSystemActionsSpec._
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class UserSystemActionsSpec extends AsyncTestSpec {

  //noinspection TypeAnnotation
  class Api {
    val listUserSystems = mockFunction[Int, Future[UserSystemResp]]
    val addUserSystems = mockFunction[Int, UserSystemUpdateReq, Future[UserSystemResp]]
    val removeUserSystems = mockFunction[Int, UserSystemUpdateReq, Future[UserSystemResp]]

    val api = new MockUserSystemApi(
      listUserSystemsMock = listUserSystems,
      addUserSystemsMock = addUserSystems,
      removeUserSystemsMock = removeUserSystems
    )
  }

  it should "dispatch UserSystemFetchedAction and UserUpdatedAction when userSystemsFetch" in {
    //given
    val api = new Api
    val actions = new UserSystemActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      UserData(
        id = Some(11),
        company = UserCompanyData(2, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      )
    )
    val expectedResp = UserSystemResp(respData)

    api.listUserSystems.expects(respData.user.id.get)
      .returning(Future.successful(expectedResp))
    dispatch.expects(UserUpdatedAction(respData.user))
    dispatch.expects(UserSystemFetchedAction(respData))
    
    //when
    val UserSystemFetchAction(FutureTask(message, future)) =
      actions.userSystemsFetch(dispatch, respData.user.id.get)
    
    //then
    message shouldBe "Fetching User Applications"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch UserSystemAddedAction and UserUpdatedAction when userSystemsAdd" in {
    //given
    val api = new Api
    val actions = new UserSystemActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      UserData(
        id = Some(11),
        company = UserCompanyData(2, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      )
    )
    val data = UserSystemUpdateReq(Set(1, 2, 3), 4)
    val expectedResp = UserSystemResp(respData)

    api.addUserSystems.expects(respData.user.id.get, data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(UserUpdatedAction(respData.user))
    dispatch.expects(UserSystemAddedAction(respData))
    
    //when
    val UserSystemAddAction(FutureTask(message, future)) =
      actions.userSystemsAdd(dispatch, respData.user.id.get, data)
    
    //then
    message shouldBe "Adding User Applications"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch UserSystemRemovedAction and UserUpdatedAction when userSystemsRemove" in {
    //given
    val api = new Api
    val actions = new UserSystemActionsTest(api.api)
    val dispatch = mockFunction[Any, Any]
    val respData = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      UserData(
        id = Some(11),
        company = UserCompanyData(2, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      )
    )
    val data = UserSystemUpdateReq(Set(1, 2, 3), 4)
    val expectedResp = UserSystemResp(respData)

    api.removeUserSystems.expects(respData.user.id.get, data)
      .returning(Future.successful(expectedResp))
    dispatch.expects(UserUpdatedAction(respData.user))
    dispatch.expects(UserSystemRemovedAction(respData))
    
    //when
    val UserSystemRemoveAction(FutureTask(message, future)) =
      actions.userSystemsRemove(dispatch, respData.user.id.get, data)
    
    //then
    message shouldBe "Removing User Applications"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object UserSystemActionsSpec {
  
  private class UserSystemActionsTest(api: UserSystemApi)
    extends UserSystemActions {

    protected def client: UserSystemApi = api
  }
}
