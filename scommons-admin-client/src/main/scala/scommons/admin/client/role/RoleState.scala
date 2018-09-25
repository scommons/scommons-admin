package scommons.admin.client.role

import scommons.admin.client.api.role.RoleData
import scommons.admin.client.role.RoleActions._

case class RoleState(rolesBySystemId: Map[Int, List[RoleData]] = Map.empty,
                     showCreatePopup: Boolean = false,
                     showEditPopup: Boolean = false) {

  def getRoles(systemId: Int): List[RoleData] = {
    rolesBySystemId.getOrElse(systemId, Nil)
  }
}

object RoleStateReducer {

  def apply(state: Option[RoleState], action: Any): RoleState = {
    reduce(state.getOrElse(RoleState()), action)
  }
  
  private def reduce(state: RoleState, action: Any): RoleState = action match {
    case a: RoleCreateRequestAction => state.copy(showCreatePopup = a.create)
    case a: RoleUpdateRequestAction => state.copy(showEditPopup = a.update)
    case RoleListFetchedAction(dataList) => state.copy(
      rolesBySystemId = dataList.groupBy(_.systemId)
    )
    case RoleCreatedAction(data) =>
      val dataList = state.getRoles(data.systemId)
      state.copy(
        rolesBySystemId = state.rolesBySystemId + (data.systemId -> (dataList :+ data)),
        showCreatePopup = false
      )
    case RoleUpdatedAction(data) =>
      val dataList = state.getRoles(data.systemId).map {
        case curr if curr.id == data.id => data
        case curr => curr
      }
      state.copy(
        rolesBySystemId = state.rolesBySystemId + (data.systemId -> dataList),
        showEditPopup = false
      )
    case _ => state
  }
}
