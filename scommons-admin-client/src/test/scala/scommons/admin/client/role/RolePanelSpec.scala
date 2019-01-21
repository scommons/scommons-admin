package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.role._
import scommons.admin.client.role.RoleActions._
import scommons.client.task.FutureTask
import scommons.client.ui.popup._
import scommons.react.test.TestSpec
import scommons.react.test.dom.util.TestDOMUtils
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils

import scala.concurrent.Future

class RolePanelSpec extends TestSpec with ShallowRendererUtils with TestDOMUtils {

  it should "dispatch RoleCreateAction when onOk in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[RoleActions]
    val state = RoleState()
    val props = RolePanelProps(dispatch, actions, state, Some(2), None)
    val comp = shallowRender(<(RolePanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, InputPopup)
    val data = RoleData(None, 2, "new role")
    val action = RoleCreateAction(
      FutureTask("Creating", Future.successful(RoleResp(data.copy(id = Some(1)))))
    )
    (actions.roleCreate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    createPopupProps.onOk(data.title)
  }

  it should "dispatch RoleCreateRequestAction(false) when onCancel in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[RoleActions]
    val state = RoleState()
    val props = RolePanelProps(dispatch, actions, state, Some(2), None)
    val comp = shallowRender(<(RolePanel())(^.wrapped := props)())
    val createPopupProps = findComponentProps(comp, InputPopup)

    //then
    dispatch.expects(RoleCreateRequestAction(create = false))
    
    //when
    createPopupProps.onCancel()
  }

  it should "dispatch RoleUpdateAction when onOk in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[RoleActions]
    val state = RoleState(List(
      RoleData(Some(1), 3, "test role 1"),
      RoleData(Some(2), 3, "test role 2")
    ).groupBy(_.systemId))
    val props = RolePanelProps(dispatch, actions, state, Some(3), Some(1))
    val comp = shallowRender(<(RolePanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, InputPopup)(1)
    val data = RoleData(Some(1), 3, "updated role")
    val action = RoleUpdateAction(
      FutureTask("Updating", Future.successful(RoleResp(data)))
    )
    (actions.roleUpdate _).expects(dispatch, data)
      .returning(action)

    //then
    dispatch.expects(action)
    
    //when
    editPopupProps.onOk(data.title)
  }

  it should "dispatch RoleUpdateRequestAction(false) when onCancel in edit popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[RoleActions]
    val state = RoleState(List(
      RoleData(Some(1), 3, "test role 1"),
      RoleData(Some(2), 3, "test role 2")
    ).groupBy(_.systemId))
    val props = RolePanelProps(dispatch, actions, state, Some(3), Some(1))
    val comp = shallowRender(<(RolePanel())(^.wrapped := props)())
    val editPopupProps = findProps(comp, InputPopup)(1)

    //then
    dispatch.expects(RoleUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "dispatch RoleListFetchAction when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[RoleActions]
    val state = RoleState()
    val props = RolePanelProps(dispatch, actions, state, None, None)
    val component = <(RolePanel())(^.wrapped := props)()
    val action = RoleListFetchAction(
      FutureTask("Fetching", Future.successful(RoleListResp(Nil)))
    )
    (actions.roleListFetch _).expects(dispatch)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    renderIntoDocument(component)
  }

  it should "not dispatch RoleListFetchAction if non empty dataList when componentDidMount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[RoleActions]
    val state = RoleState(List(
      RoleData(Some(1), 3, "test role 1"),
      RoleData(Some(2), 3, "test role 2")
    ).groupBy(_.systemId))
    val props = RolePanelProps(dispatch, actions, state, Some(3), None)
    val component = <(RolePanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    renderIntoDocument(component)
  }

  it should "render component" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[RoleActions]
    val state = RoleState(List(
      RoleData(Some(1), 3, "test role 1"),
      RoleData(Some(2), 3, "test role 2")
    ).groupBy(_.systemId))
    val props = RolePanelProps(dispatch, actions, state, Some(3), Some(1))
    val component = <(RolePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertRolePanel(result, props)
  }

  it should "render component and show create popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[RoleActions]
    val state = RoleState(
      rolesBySystemId = List(
        RoleData(Some(1), 3, "test role 1"),
        RoleData(Some(2), 3, "test role 2")
      ).groupBy(_.systemId),
      showCreatePopup = true
    )
    val props = RolePanelProps(dispatch, actions, state, Some(3), None)
    val component = <(RolePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertRolePanel(result, props)
  }

  it should "render component and show edit popup" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[RoleActions]
    val state = RoleState(
      rolesBySystemId = List(
        RoleData(Some(1), 3, "test role 1"),
        RoleData(Some(2), 3, "test role 2")
      ).groupBy(_.systemId),
      showEditPopup = true
    )
    val props = RolePanelProps(dispatch, actions, state, Some(3), Some(1))
    val component = <(RolePanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertRolePanel(result, props)
  }

  private def assertRolePanel(result: ShallowInstance, props: RolePanelProps): Unit = {
    val selectedData = props.selectedSystemId.flatMap { systemId =>
      props.state.rolesBySystemId.getOrElse(systemId, Nil)
        .find(_.id == props.selectedId)
    }

    def assertComponents(createPopup: ShallowInstance,
                         editPopup: Option[ShallowInstance]): Assertion = {

      assertComponent(createPopup, InputPopup) {
        case InputPopupProps(show, message, _, _, placeholder, initialValue) =>
          show shouldBe props.state.showCreatePopup
          message shouldBe "Enter Role title:"
          placeholder shouldBe None
          initialValue shouldBe "NEW_ROLE"
      }
      
      editPopup.isEmpty shouldBe selectedData.isEmpty
      selectedData.foreach { data =>
        assertComponent(editPopup.get, InputPopup) {
          case InputPopupProps(show, message, _, _, placeholder, initialValue) =>
            show shouldBe props.state.showEditPopup
            message shouldBe "Enter new Role title:"
            placeholder shouldBe None
            initialValue shouldBe data.title
        }
      }
      Succeeded
    }
    
    assertNativeComponent(result, <.div()(), {
      case List(createPopup) => assertComponents(createPopup, None)
      case List(createPopup, editPopup) => assertComponents(createPopup, Some(editPopup))
    })
  }
}
