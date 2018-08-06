package scommons.admin.client.company

import scommons.admin.client.api.company.CompanyData
import scommons.admin.client.company.CompanyActions._

case class CompanyState(dataList: List[CompanyData] = Nil,
                        offset: Option[Int] = None,
                        totalCount: Option[Int] = None,
                        selectedId: Option[Int] = None,
                        showCreatePopup: Boolean = false,
                        showEditPopup: Boolean = false)

object CompanyStateReducer {

  def apply(state: Option[CompanyState], action: Any): CompanyState = {
    reduce(state.getOrElse(CompanyState()), action)
  }
  
  private def reduce(state: CompanyState, action: Any): CompanyState = action match {
    case a: CompanyCreateRequestAction => state.copy(showCreatePopup = a.create)
    case a: CompanyUpdateRequestAction => state.copy(showEditPopup = a.update)
    case a: CompanySelectedAction => state.copy(selectedId = Some(a.id))
    case a: CompanyListFetchAction => state.copy(offset = a.offset)
    case CompanyListFetchedAction(dataList, totalCount) => state.copy(
      dataList = dataList,
      totalCount = totalCount.orElse(state.totalCount)
    )
    case CompanyCreatedAction(data) => state.copy(dataList = state.dataList :+ data)
    case CompanyUpdatedAction(data) => state.copy(dataList = state.dataList.map {
      case curr if curr.id == data.id => data
      case curr => curr
    })
    case _ => state
  }
}
