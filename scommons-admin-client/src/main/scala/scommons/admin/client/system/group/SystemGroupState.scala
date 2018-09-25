package scommons.admin.client.system.group

import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.system.group.SystemGroupActions._

case class SystemGroupState(dataList: List[SystemGroupData] = Nil,
                            showCreatePopup: Boolean = false,
                            showEditPopup: Boolean = false)

object SystemGroupStateReducer {

  def apply(state: Option[SystemGroupState], action: Any): SystemGroupState = {
    reduce(state.getOrElse(SystemGroupState()), action)
  }
  
  private def reduce(state: SystemGroupState, action: Any): SystemGroupState = action match {
    case a: SystemGroupCreateRequestAction => state.copy(showCreatePopup = a.create)
    case a: SystemGroupUpdateRequestAction => state.copy(showEditPopup = a.update)
    case SystemGroupListFetchedAction(dataList) => state.copy(
      dataList = dataList
    )
    case SystemGroupCreatedAction(data) => state.copy(
      dataList = state.dataList :+ data,
      showCreatePopup = false
    )
    case SystemGroupUpdatedAction(data) => state.copy(
      dataList = state.dataList.map {
        case curr if curr.id == data.id => data
        case curr => curr
      },
      showEditPopup = false
    )
    case _ => state
  }
}
