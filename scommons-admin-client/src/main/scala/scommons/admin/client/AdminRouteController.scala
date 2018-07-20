package scommons.admin.client

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.client.app.{AppBrowseController, AppBrowseControllerProps, BaseStateController}
import scommons.client.ui.Buttons

object AdminRouteController
  extends BaseStateController[AdminStateDef, AppBrowseControllerProps] {

  lazy val component: ReactClass = AppBrowseController()

  def mapStateToProps(dispatch: Dispatch)
                     (state: AdminStateDef, props: Props[Unit]): AppBrowseControllerProps = {

    AppBrowseControllerProps(
      List(Buttons.REFRESH, Buttons.ADD, Buttons.REMOVE, Buttons.EDIT),
      state.treeRoots,
      dispatch
    )
  }
}
