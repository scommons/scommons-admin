package scommons.admin.client

import scommons.admin.client.action.ApiActions
import scommons.admin.client.company._
import scommons.client.task.AbstractTask.AbstractTaskKey
import scommons.client.task.TaskAction
import scommons.client.ui.tree._
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.{ActionsData, BrowsePath}

case class AdminState(currentTask: Option[AbstractTaskKey],
                      companyState: CompanyState)

object AdminStateReducer {

  val companiesItem = BrowseTreeItemData(
    "Companies",
    BrowsePath("/companies"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.REFRESH.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(ApiActions.companyListFetch(dispatch))
    }),
    Some(CompanyPanelController())
  )

  def getTreeRoots(state: AdminState): List[BrowseTreeData] = List(
    companiesItem
  )

  def reduce(state: Option[AdminState], action: Any): AdminState = AdminState(
    currentTask = currentTaskReducer(state.flatMap(_.currentTask), action),
    companyState = CompanyStateReducer(state.map(_.companyState), action)
  )

  private def currentTaskReducer(currentTask: Option[AbstractTaskKey],
                                 action: Any): Option[AbstractTaskKey] = action match {

    case a: TaskAction => Some(a.task.key)
    case _ => None
  }
}
