package scommons.admin.client.company

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.admin.client.AdminStateDef
import scommons.admin.client.company.action.CompanyActions
import scommons.client.test.TestSpec

class CompanyPanelControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val apiActions = mock[CompanyActions]
    val controller = new CompanyPanelController(apiActions)
    
    //when & then
    controller.component shouldBe CompanyPanel()
  }
  
  it should "map state to props" in {
    //given
    val apiActions = mock[CompanyActions]
    val props = mock[Props[Unit]]
    val controller = new CompanyPanelController(apiActions)
    val dispatch = mock[Dispatch]
    val companyState = mock[CompanyState]
    val state = mock[AdminStateDef]
    (state.companyState _).expects().returning(companyState)

    //when
    val result = controller.mapStateToProps(dispatch)(state, props)
    
    //then
    inside(result) { case CompanyPanelProps(disp, actions, compState) =>
      disp shouldBe dispatch
      actions shouldBe apiActions
      compState shouldBe companyState
    }
  }
}
