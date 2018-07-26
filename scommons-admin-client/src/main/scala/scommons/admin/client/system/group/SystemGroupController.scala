package scommons.admin.client.system.group

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import io.github.shogowada.scalajs.reactjs.router.RouterProps
import scommons.admin.client.AdminStateDef
import scommons.admin.client.system.group.SystemGroupController._
import scommons.admin.client.system.group.action.SystemGroupActions
import scommons.client.app.BaseStateController

class SystemGroupController(apiActions: SystemGroupActions)
  extends BaseStateController[AdminStateDef, SystemGroupPanelProps]
    with RouterProps {

  lazy val component: ReactClass = SystemGroupPanel()

  def mapStateToProps(dispatch: Dispatch)
                     (state: AdminStateDef, props: Props[Unit]): SystemGroupPanelProps = {

    val path = props.location.pathname
    
    SystemGroupPanelProps(dispatch, apiActions, state.systemGroupState, extractId(path))
  }
}

object SystemGroupController {
  
  private val idRegex = "/environments/(\\d+)".r
  
  private[group] def extractId(path: String): Option[Int] = {
    for {
      idRegex(id) <- idRegex.findPrefixMatchOf(path)
    } yield {
      id.toInt
    }
  }
}
