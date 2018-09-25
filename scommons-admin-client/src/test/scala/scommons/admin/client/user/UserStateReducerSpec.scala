package scommons.admin.client.user

import scommons.admin.client.api.user._
import scommons.admin.client.user.UserActions._
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec

import scala.concurrent.Future

class UserStateReducerSpec extends TestSpec {

  private val reduce = UserStateReducer.apply _
  
  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe UserState()
  }
  
  it should "set showCreatePopup when UserCreateRequestAction" in {
    //when & then
    reduce(Some(UserState()), UserCreateRequestAction(true)) shouldBe {
      UserState(showCreatePopup = true)
    }
    reduce(Some(UserState(showCreatePopup = true)), UserCreateRequestAction(false)) shouldBe {
      UserState()
    }
  }
  
  it should "set showEditPopup when UserUpdateRequestAction" in {
    //when & then
    reduce(Some(UserState()), UserUpdateRequestAction(true)) shouldBe {
      UserState(showEditPopup = true)
    }
    reduce(Some(UserState(showEditPopup = true)), UserUpdateRequestAction(false)) shouldBe {
      UserState()
    }
  }
  
  it should "set selected and update dataList when UserFetchedAction" in {
    //given
    val dataList = List(
      UserData(
        id = Some(11),
        company = UserCompanyData(1, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      ),
      UserData(
        id = Some(12),
        company = UserCompanyData(1, "Test Company"),
        login = "test_login2",
        password = "test2",
        active = true
      )
    )
    val data = UserDetailsData(
      user = UserData(
        id = Some(11),
        company = UserCompanyData(1, "Test Company"),
        login = "updated_login",
        password = "updated_password",
        active = true
      ),
      profile = UserProfileData(
        email = "test@email.com",
        firstName = "Firstname",
        lastName = "Lastname",
        phone = Some("0123 456 789")
      )
    )
    
    //when & then
    reduce(Some(UserState(dataList = dataList)), UserFetchedAction(data)) shouldBe UserState(
      dataList = dataList.map {
        case curr if curr.id == data.user.id => data.user
        case curr => curr
      },
      selected = Some(data)
    )
  }
  
  it should "set offset when UserListFetchAction" in {
    //given
    val task = FutureTask("test task", Future.successful(UserListResp(Nil, None)))
    val offset = Some(123)
    
    //when & then
    reduce(Some(UserState()), UserListFetchAction(task, offset)) shouldBe UserState(
      offset = offset
    )
  }
  
  it should "set dataList, totalCount and reset selected when UserListFetchedAction" in {
    //given
    val dataList = List(UserData(
      id = Some(11),
      company = UserCompanyData(1, "Test Company"),
      login = "test_login",
      password = "test",
      active = true
    ))
    val totalCount = Some(123)
    val selected = Some(UserDetailsData(
      user = UserData(
        id = Some(11),
        company = UserCompanyData(1, "Test Company"),
        login = "updated_login",
        password = "updated_password",
        active = true
      ),
      profile = UserProfileData(
        email = "test@email.com",
        firstName = "Firstname",
        lastName = "Lastname",
        phone = Some("0123 456 789")
      )
    ))
    
    //when & then
    reduce(Some(UserState()), UserListFetchedAction(dataList, totalCount)) shouldBe UserState(
      dataList = dataList,
      totalCount = totalCount
    )

    //when & then
    reduce(Some(UserState(
      totalCount = totalCount,
      selected = selected
    )), UserListFetchedAction(dataList, None)) shouldBe UserState(
      dataList = dataList,
      totalCount = totalCount,
      selected = None
    )
  }

  it should "append new data to the dataList and set selected when UserCreatedAction" in {
    //given
    val dataList = List(UserData(
      id = Some(11),
      company = UserCompanyData(1, "Test Company"),
      login = "test_login",
      password = "test",
      active = true
    ))
    val data = UserDetailsData(
      user = UserData(
        id = Some(12),
        company = UserCompanyData(1, "Test Company"),
        login = "test_login2",
        password = "test",
        active = true
      ),
      profile = UserProfileData(
        email = "test2@email.com",
        firstName = "Firstname",
        lastName = "Lastname",
        phone = Some("0123 456 789")
      )
    )

    //when & then
    reduce(Some(UserState(dataList, showCreatePopup = true)), UserCreatedAction(data)) shouldBe UserState(
      dataList = dataList :+ data.user,
      selected = Some(data)
    )
  }
  
  it should "update dataList and selected when UserUpdatedAction" in {
    //given
    val existingData = UserData(
      id = Some(12),
      company = UserCompanyData(1, "Test Company"),
      login = "test_login2",
      password = "test",
      active = true
    )
    val dataList = List(
      UserData(
        id = Some(11),
        company = UserCompanyData(1, "Test Company"),
        login = "test_login",
        password = "test",
        active = true
      ),
      existingData
    )
    val data = UserDetailsData(
        user = UserData(
        id = Some(11),
        company = UserCompanyData(1, "Test Company"),
        login = "updated_login",
        password = "test",
        active = true
      ),
      profile = UserProfileData(
        email = "test2@email.com",
        firstName = "Firstname",
        lastName = "Lastname",
        phone = Some("0123 456 789")
      )
    )

    //when & then
    reduce(Some(UserState(dataList, showEditPopup = true)), UserUpdatedAction(data)) shouldBe UserState(
      dataList = List(data.user, existingData),
      selected = Some(data)
    )
  }
}
