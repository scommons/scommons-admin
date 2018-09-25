package scommons.admin.client.system

import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.SystemActions._

case class SystemState(systemsByParentId: Map[Int, List[SystemData]] = Map.empty,
                       showCreatePopup: Boolean = false,
                       showEditPopup: Boolean = false) {
  
  def getSystems(parentId: Int): List[SystemData] = {
    systemsByParentId.getOrElse(parentId, Nil)
  }
}

object SystemStateReducer {

  def apply(state: Option[SystemState], action: Any): SystemState = {
    reduce(state.getOrElse(SystemState()), action)
  }
  
  private def reduce(state: SystemState, action: Any): SystemState = action match {
    case a: SystemCreateRequestAction => state.copy(showCreatePopup = a.create)
    case a: SystemUpdateRequestAction => state.copy(showEditPopup = a.update)
    case SystemListFetchedAction(dataList) => state.copy(
      systemsByParentId = dataList.groupBy(_.parentId)
    )
    case SystemCreatedAction(data) =>
      val dataList = state.getSystems(data.parentId)
      state.copy(
        systemsByParentId = state.systemsByParentId + (data.parentId -> (dataList :+ data)),
        showCreatePopup = false
      )
    case SystemUpdatedAction(data) =>
      val dataList = state.getSystems(data.parentId).map {
        case curr if curr.id == data.id => data
        case curr => curr
      }
      state.copy(
        systemsByParentId = state.systemsByParentId + (data.parentId -> dataList),
        showEditPopup = false
      )
    case _ => state
  }
}
