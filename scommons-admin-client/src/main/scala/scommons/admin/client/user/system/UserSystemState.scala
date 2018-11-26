package scommons.admin.client.user.system

import scommons.admin.client.api.user.system.UserSystemData
import scommons.admin.client.user.system.UserSystemActions._

case class UserSystemState(systems: List[UserSystemData] = Nil,
                           userId: Option[Int] = None)

object UserSystemStateReducer {

  def apply(state: Option[UserSystemState], action: Any): UserSystemState = {
    reduce(state.getOrElse(UserSystemState()), action)
  }
  
  private def reduce(state: UserSystemState, action: Any): UserSystemState = action match {
    case UserSystemFetchedAction(data) => state.copy(
      systems = data.systems,
      userId = data.user.id
    )
    case UserSystemAddedAction(data) => state.copy(
      systems = data.systems,
      userId = data.user.id
    )
    case UserSystemRemovedAction(data) => state.copy(
      systems = data.systems,
      userId = data.user.id
    )
    case _ => state
  }
}
