package scommons.admin.client.user.system

import scommons.admin.client.api.user.system._
import scommons.admin.client.api.user.{UserCompanyData, UserData}
import scommons.admin.client.user.system.UserSystemActions._
import scommons.react.test.TestSpec

class UserSystemStateReducerSpec extends TestSpec {

  private val reduce = UserSystemStateReducer.apply _

  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe UserSystemState()
  }
  
  it should "set data in state when UserSystemFetchedAction" in {
    //given
    val data = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      UserData(
        id = Some(11),
        company = UserCompanyData(2, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      )
    )
    
    //when & then
    reduce(Some(UserSystemState()), UserSystemFetchedAction(data)) shouldBe {
      UserSystemState(
        systems = data.systems,
        userId = data.user.id
      )
    }
  }
  
  it should "set data in state when UserSystemAddedAction" in {
    //given
    val data = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      UserData(
        id = Some(11),
        company = UserCompanyData(2, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      )
    )
    
    //when & then
    reduce(Some(UserSystemState()), UserSystemAddedAction(data)) shouldBe {
      UserSystemState(
        systems = data.systems,
        userId = data.user.id
      )
    }
  }
  
  it should "set data in state when UserSystemRemovedAction" in {
    //given
    val data = UserSystemRespData(
      List(UserSystemData(1, "test_app", isSelected = false)),
      UserData(
        id = Some(11),
        company = UserCompanyData(2, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      )
    )
    
    //when & then
    reduce(Some(UserSystemState()), UserSystemRemovedAction(data)) shouldBe {
      UserSystemState(
        systems = data.systems,
        userId = data.user.id
      )
    }
  }
}
