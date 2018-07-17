package scommons.admin.client

import scommons.admin.client.company._
import scommons.admin.client.company.action.CompanyActions
import scommons.client.task.AbstractTask.AbstractTaskKey
import scommons.client.task.TaskAction
import scommons.client.ui.tree._
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.{ActionsData, BrowsePath}

trait AdminStateDef {

  def currentTask: Option[AbstractTaskKey]
  def treeRoots: List[BrowseTreeData]
  def companyState: CompanyState
}

case class AdminState(currentTask: Option[AbstractTaskKey],
                      treeRoots: List[BrowseTreeData],
                      companyState: CompanyState) extends AdminStateDef

class AdminStateReducer(apiActions: CompanyActions) {

  lazy val companiesItem = BrowseTreeItemData(
    "Companies",
    BrowsePath("/companies"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.REFRESH.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.companyListFetch(dispatch, None, None))
    }),
    Some(new CompanyPanelController(apiActions)())
  )

  def reduce(state: Option[AdminState], action: Any): AdminState = AdminState(
    currentTask = currentTaskReducer(state.flatMap(_.currentTask), action),
    treeRoots = treeRootsReducer(state.map(_.treeRoots), action),
    companyState = CompanyStateReducer(state.map(_.companyState), action)
  )

  private def currentTaskReducer(currentTask: Option[AbstractTaskKey],
                                 action: Any): Option[AbstractTaskKey] = action match {

    case a: TaskAction => Some(a.task.key)
    case _ => None
  }

  private def treeRootsReducer(state: Option[List[BrowseTreeData]],
                               action: Any): List[BrowseTreeData] = action match {

    case _ => state.getOrElse(List(
      companiesItem
    ))
  }
}
