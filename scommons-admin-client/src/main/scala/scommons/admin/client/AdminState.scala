package scommons.admin.client

import scommons.admin.client.action.ApiActions
import scommons.admin.client.api.system.group.SystemGroupData
import scommons.admin.client.company._
import scommons.admin.client.system.group._
import scommons.admin.client.system.group.action._
import scommons.client.task.AbstractTask.AbstractTaskKey
import scommons.client.task.TaskAction
import scommons.client.ui.tree._
import scommons.client.ui.{ButtonImagesCss, Buttons}
import scommons.client.util.{ActionsData, BrowsePath}

trait AdminStateDef {

  def currentTask: Option[AbstractTaskKey]
  def companyState: CompanyState
  def systemGroupState: SystemGroupState
}

case class AdminState(currentTask: Option[AbstractTaskKey],
                      companyState: CompanyState,
                      systemGroupState: SystemGroupState) extends AdminStateDef

class AdminStateReducer(apiActions: ApiActions) {

  lazy val companiesItem = BrowseTreeItemData(
    "Companies",
    BrowsePath("/companies"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.REFRESH.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.companyListFetch(dispatch, None, None))
    }),
    Some(new CompanyPanelController(apiActions)())
  )

  lazy val environmentsItem = BrowseTreeNodeData(
    "Environments",
    BrowsePath("/environments"),
    Some(AdminImagesCss.computer),
    ActionsData(Set(Buttons.REFRESH.command, Buttons.ADD.command), dispatch => {
      case Buttons.REFRESH.command => dispatch(apiActions.systemGroupListFetch(dispatch))
      case Buttons.ADD.command => dispatch(SystemGroupCreateRequestAction(create = true))
    }),
    None
  )
  lazy val environmentItem = BrowseTreeItemData(
    "",
    BrowsePath("/environments/0"),
    Some(ButtonImagesCss.folder),
    ActionsData(Set(Buttons.EDIT.command), dispatch => {
      case Buttons.EDIT.command => dispatch(SystemGroupUpdateRequestAction(update = true))
    }),
    None
  )

  private def getEnvironmentItem(data: SystemGroupData): BrowseTreeItemData = {
    environmentItem.copy(
      text = data.name,
      path = BrowsePath(s"/environments/${data.id.get}")
    )
  }

  def getTreeRoots(state: AdminStateDef): List[BrowseTreeData] = List(
    companiesItem,
    environmentsItem.copy(
      children = state.systemGroupState.dataList.map(getEnvironmentItem)
    )
  )

  def getInitiallyOpenedNodes: Set[BrowsePath] = Set(
    environmentsItem.path
  )

  def reduce(state: Option[AdminState], action: Any): AdminState = AdminState(
    currentTask = currentTaskReducer(state.flatMap(_.currentTask), action),
    companyState = CompanyStateReducer(state.map(_.companyState), action),
    systemGroupState = SystemGroupStateReducer(state.map(_.systemGroupState), action)
  )

  private def currentTaskReducer(currentTask: Option[AbstractTaskKey],
                                 action: Any): Option[AbstractTaskKey] = action match {

    case a: TaskAction => Some(a.task.key)
    case _ => None
  }
}
