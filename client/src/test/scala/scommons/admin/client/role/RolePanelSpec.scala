package scommons.admin.client.role

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest._
import scommons.admin.client.api.role._
import scommons.admin.client.role.RoleActions._
import scommons.client.ui.popup._
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.{ShallowRendererUtils, TestRendererUtils}

import scala.concurrent.Future

class RolePanelSpec extends TestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "dispatch RoleCreateAction when onOk in create popup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[RoleActions]
    val state = RoleState(showCreatePopup = true)
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
    val state = RoleState(showCreatePopup = true)
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
    val state = RoleState(
      rolesBySystemId = List(
        RoleData(Some(1), 3, "test role 1"),
        RoleData(Some(2), 3, "test role 2")
      ).groupBy(_.systemId),
      showEditPopup = true
    )
    val props = RolePanelProps(dispatch, actions, state, Some(3), Some(1))
    val comp = shallowRender(<(RolePanel())(^.wrapped := props)())
    val editPopupProps = findComponentProps(comp, InputPopup)
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
    val state = RoleState(
      rolesBySystemId = List(
        RoleData(Some(1), 3, "test role 1"),
        RoleData(Some(2), 3, "test role 2")
      ).groupBy(_.systemId),
      showEditPopup = true
    )
    val props = RolePanelProps(dispatch, actions, state, Some(3), Some(1))
    val comp = shallowRender(<(RolePanel())(^.wrapped := props)())
    val editPopupProps = findComponentProps(comp, InputPopup)

    //then
    dispatch.expects(RoleUpdateRequestAction(update = false))
    
    //when
    editPopupProps.onCancel()
  }

  it should "dispatch RoleListFetchAction if empty rolesBySystemId when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[RoleActions]
    val state = RoleState()
    val props = RolePanelProps(dispatch, actions, state, None, None)
    val action = RoleListFetchAction(
      FutureTask("Fetching", Future.successful(RoleListResp(Nil)))
    )
    (actions.roleListFetch _).expects(dispatch)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    val renderer = createTestRenderer(<(RolePanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()
  }

  it should "not dispatch RoleListFetchAction if non empty rolesBySystemId when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[RoleActions]
    val state = RoleState(List(
      RoleData(Some(1), 3, "test role 1"),
      RoleData(Some(2), 3, "test role 2")
    ).groupBy(_.systemId))
    val props = RolePanelProps(dispatch, actions, state, Some(3), None)

    //then
    dispatch.expects(*).never()

    //when
    val renderer = createTestRenderer(<(RolePanel())(^.wrapped := props)())
    
    //cleanup
    renderer.unmount()
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

    def assertComponents(createPopup: Option[ShallowInstance],
                         editPopup: Option[ShallowInstance]): Assertion = {

      createPopup.isDefined shouldBe props.state.showCreatePopup
      createPopup.foreach { cp =>
        assertComponent(cp, InputPopup) {
          case InputPopupProps(message, _, _, placeholder, initialValue) =>
            message shouldBe "Enter Role title:"
            placeholder shouldBe None
            initialValue shouldBe "NEW_ROLE"
        }
      }
      
      editPopup.isDefined shouldBe (selectedData.isDefined && props.state.showEditPopup)
      selectedData.foreach { data =>
        editPopup.foreach { ep =>
          assertComponent(ep, InputPopup) {
            case InputPopupProps(message, _, _, placeholder, initialValue) =>
              message shouldBe "Enter new Role title:"
              placeholder shouldBe None
              initialValue shouldBe data.title
          }
        }
      }
      Succeeded
    }
    
    assertNativeComponent(result, <.>()(), { children: List[ShallowInstance] =>
      children match {
        case List(createPopup) if props.state.showCreatePopup => assertComponents(Some(createPopup), None)
        case List(editPopup) if props.state.showEditPopup => assertComponents(None, Some(editPopup))
        case Nil => assertComponents(None, None)
      }
    })
  }
}
