package scommons.admin.client.system

import scommons.admin.client.api.system.SystemData
import scommons.admin.client.system.SystemActions._

case class SystemState(systemsByParentId: Map[Int, List[SystemData]] = Map.empty,
                       showCreatePopup: Boolean = false,
                       showEditPopup: Boolean = false)

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
      val dataList = state.systemsByParentId.getOrElse(data.parentId, Nil)
      state.copy(
        systemsByParentId = state.systemsByParentId + (data.parentId -> (dataList :+ data))
      )
    case SystemUpdatedAction(data) =>
      val dataList = state.systemsByParentId.getOrElse(data.parentId, Nil).map {
        case curr if curr.id == data.id => data
        case curr => curr
      }
      state.copy(
        systemsByParentId = state.systemsByParentId + (data.parentId -> dataList)
      )
    case _ => state
  }
}
