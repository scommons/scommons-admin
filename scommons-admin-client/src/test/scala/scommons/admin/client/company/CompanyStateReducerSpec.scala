package scommons.admin.client.company

import scommons.admin.client.api.company.{CompanyData, CompanyListResp}
import scommons.admin.client.company.CompanyActions._
import scommons.client.task.FutureTask
import scommons.client.test.TestSpec

import scala.concurrent.Future

class CompanyStateReducerSpec extends TestSpec {

  private val reduce = CompanyStateReducer.apply _
  
  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe CompanyState()
  }
  
  it should "set showCreatePopup when CompanyCreateRequestAction" in {
    //when & then
    reduce(Some(CompanyState()), CompanyCreateRequestAction(true)) shouldBe {
      CompanyState(showCreatePopup = true)
    }
    reduce(Some(CompanyState(showCreatePopup = true)), CompanyCreateRequestAction(false)) shouldBe {
      CompanyState()
    }
  }
  
  it should "set showEditPopup when CompanyUpdateRequestAction" in {
    //when & then
    reduce(Some(CompanyState()), CompanyUpdateRequestAction(true)) shouldBe {
      CompanyState(showEditPopup = true)
    }
    reduce(Some(CompanyState(showEditPopup = true)), CompanyUpdateRequestAction(false)) shouldBe {
      CompanyState()
    }
  }
  
  it should "set selectedId when CompanySelectedAction" in {
    //given
    val selectedId = 123
    
    //when & then
    reduce(Some(CompanyState()), CompanySelectedAction(selectedId)) shouldBe {
      CompanyState(selectedId = Some(selectedId))
    }
  }
  
  it should "set offset when CompanyListFetchAction" in {
    //given
    val task = FutureTask("test task", Future.successful(CompanyListResp(Nil)))
    val offset = Some(123)
    
    //when & then
    reduce(Some(CompanyState()), CompanyListFetchAction(task, offset)) shouldBe {
      CompanyState(offset = offset)
    }
  }
  
  it should "set dataList and totalCount when CompanyListFetchedAction" in {
    //given
    val dataList = List(CompanyData(Some(1), "test name"))
    val totalCount = Some(123)
    
    //when & then
    reduce(Some(CompanyState()), CompanyListFetchedAction(dataList, totalCount)) shouldBe {
      CompanyState(
        dataList = dataList,
        totalCount = totalCount
      )
    }
    reduce(Some(CompanyState(totalCount = totalCount)), CompanyListFetchedAction(dataList, None)) shouldBe {
      CompanyState(
        dataList = dataList,
        totalCount = totalCount
      )
    }
  }

  it should "append new data to the dataList when CompanyCreatedAction" in {
    //given
    val dataList = List(CompanyData(Some(1), "test name"))
    val data = CompanyData(Some(2), "test name 2")

    //when & then
    reduce(Some(CompanyState(dataList = dataList, showCreatePopup = true)), CompanyCreatedAction(data)) shouldBe {
      CompanyState(
        dataList = dataList :+ data
      )
    }
  }
  
  it should "update dataList when CompanyUpdatedAction" in {
    //given
    val existingData = CompanyData(Some(2), "test name 2")
    val dataList = List(
      CompanyData(Some(1), "test name"),
      existingData
    )
    val data = CompanyData(Some(1), "updated test name")

    //when & then
    reduce(Some(CompanyState(dataList = dataList, showEditPopup = true)), CompanyUpdatedAction(data)) shouldBe {
      CompanyState(
        dataList = List(
          data,
          existingData
        )
      )
    }
  }
}
