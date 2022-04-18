package scommons.admin.client.role.permission

import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.role.RoleData
import scommons.admin.client.api.role.permission._
import scommons.admin.client.role.permission.RolePermissionActions._
import scommons.admin.client.role.permission.RolePermissionPanel._
import scommons.client.ui.TriState._
import scommons.client.ui.tree._
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test._

import scala.concurrent.Future

class RolePermissionPanelSpec extends TestSpec with TestRendererUtils {

  RolePermissionPanel.checkBoxTreeComp = mockUiComponent("CheckBoxTree")

  //noinspection TypeAnnotation
  class Actions {
    val rolePermissionsFetch = mockFunction[Dispatch, Int, RolePermissionFetchAction]
    val rolePermissionsAdd = mockFunction[Dispatch, Int, RolePermissionUpdateReq, RolePermissionAddAction]
    val rolePermissionsRemove = mockFunction[Dispatch, Int, RolePermissionUpdateReq, RolePermissionRemoveAction]

    val actions = new MockRolePermissionActions(
      rolePermissionsFetchMock = rolePermissionsFetch,
      rolePermissionsAddMock = rolePermissionsAdd,
      rolePermissionsRemoveMock = rolePermissionsRemove
    )
  }

  it should "dispatch RolePermissionAddAction if Selected when onChange" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val roleId = 11
    val version = 123
    val respData = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = false, "test permission", isEnabled = false)
    ),
      RoleData(Some(roleId), 22, "test role", version = Some(version))
    )
    val state = RolePermissionState(
      permissionsByParentId = respData.permissions.groupBy(_.parentId),
      role = Some(respData.role)
    )
    val props = RolePermissionPanelProps(dispatch, actions.actions, state, roleId)
    val comp = testRender(<(RolePermissionPanel())(^.wrapped := props)())
    val checkBoxTreeProps = findComponentProps(comp, checkBoxTreeComp)
    val data = RolePermissionUpdateReq(Set(1), version)
    val action = RolePermissionAddAction(
      FutureTask("Test", Future.successful(RolePermissionResp(respData)))
    )
    actions.rolePermissionsAdd.expects(dispatch, roleId, data)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    checkBoxTreeProps.onChange(CheckBoxTreeNodeData("1", Deselected, "test"), Selected)
  }

  it should "dispatch RolePermissionRemoveAction if Deselected when onChange" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val roleId = 11
    val version = 123
    val respData = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = false, "test permission", isEnabled = true)
    ),
      RoleData(Some(roleId), 22, "test role", version = Some(version))
    )
    val state = RolePermissionState(
      permissionsByParentId = respData.permissions.groupBy(_.parentId),
      role = Some(respData.role)
    )
    val props = RolePermissionPanelProps(dispatch, actions.actions, state, roleId)
    val comp = testRender(<(RolePermissionPanel())(^.wrapped := props)())
    val checkBoxTreeProps = findComponentProps(comp, checkBoxTreeComp)
    val data = RolePermissionUpdateReq(Set(1), version)
    val action = RolePermissionRemoveAction(
      FutureTask("Test", Future.successful(RolePermissionResp(respData)))
    )
    actions.rolePermissionsRemove.expects(dispatch, roleId, data)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    checkBoxTreeProps.onChange(CheckBoxTreeNodeData("1", Selected, "test"), Deselected)
  }

  it should "dispatch RolePermissionFetchAction if different selected role when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val roleId = 11
    val respData = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = false, "test permission", isEnabled = true)
    ),
      RoleData(Some(roleId), 22, "test role", version = Some(123))
    )
    val state = RolePermissionState()
    val props = RolePermissionPanelProps(dispatch, actions.actions, state, roleId)
    val component = <(RolePermissionPanel())(^.wrapped := props)()
    val action = RolePermissionFetchAction(
      FutureTask("Fetching", Future.successful(RolePermissionResp(respData)))
    )
    actions.rolePermissionsFetch.expects(dispatch, roleId)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    testRender(component)
  }

  it should "not dispatch RolePermissionFetchAction if same selected role when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val roleId = 11
    val respData = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = false, "test permission", isEnabled = true)
    ),
      RoleData(Some(roleId), 22, "test role", version = Some(123))
    )
    val state = RolePermissionState(
      permissionsByParentId = respData.permissions.groupBy(_.parentId),
      role = Some(respData.role)
    )
    val props = RolePermissionPanelProps(dispatch, actions.actions, state, roleId)
    val component = <(RolePermissionPanel())(^.wrapped := props)()

    //then
    dispatch.expects(*).never()

    //when
    testRender(component)
  }

  it should "dispatch RolePermissionFetchAction if different selected role when update" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val roleId = 11
    val respData = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = false, "test permission", isEnabled = true)
    ),
      RoleData(Some(111), 22, "test role", version = Some(123))
    )
    val state = RolePermissionState(
      permissionsByParentId = respData.permissions.groupBy(_.parentId),
      role = Some(respData.role)
    )
    val prevProps = RolePermissionPanelProps(dispatch, actions.actions, state, respData.role.id.get)
    val renderer = createTestRenderer(<(RolePermissionPanel())(^.wrapped := prevProps)())
    val props = RolePermissionPanelProps(dispatch, actions.actions, state, roleId)
    props.selectedRoleId should not be prevProps.selectedRoleId
    
    val action = RolePermissionFetchAction(
      FutureTask("Fetching", Future.successful(RolePermissionResp(respData)))
    )
    actions.rolePermissionsFetch.expects(dispatch, roleId)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    TestRenderer.act { () =>
      renderer.update(<(RolePermissionPanel())(^.wrapped := props)())
    }
  }

  it should "not dispatch RolePermissionFetchAction if same selected role when update" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val roleId = 11
    val respData = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = false, "test permission", isEnabled = true)
    ),
      RoleData(Some(roleId), 22, "test role", version = Some(123))
    )
    val state = RolePermissionState(
      permissionsByParentId = respData.permissions.groupBy(_.parentId),
      role = Some(respData.role)
    )
    val prevProps = RolePermissionPanelProps(dispatch, actions.actions, state, respData.role.id.get)
    val renderer = createTestRenderer(<(RolePermissionPanel())(^.wrapped := prevProps)())
    val props = RolePermissionPanelProps(dispatch, mock[RolePermissionActions], state, roleId)
    props should not be prevProps
    props.selectedRoleId shouldBe prevProps.selectedRoleId
    
    //then
    dispatch.expects(*).never()

    //when
    TestRenderer.act { () =>
      renderer.update(<(RolePermissionPanel())(^.wrapped := props)())
    }
  }

  it should "render component" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = new Actions
    val data = RolePermissionRespData(List(
      RolePermissionData(1, None, isNode = true, "test permission node", isEnabled = false),
      RolePermissionData(2, Some(1), isNode = false, "test permission 1", isEnabled = false),
      RolePermissionData(3, Some(1), isNode = false, "test permission 2", isEnabled = true)
    ),
      RoleData(Some(11), 22, "test title")
    )
    val state = RolePermissionState(
      permissionsByParentId = data.permissions.groupBy(_.parentId),
      role = Some(data.role)
    )
    val props = RolePermissionPanelProps(dispatch, actions.actions, state, data.role.id.get)
    val component = <(RolePermissionPanel())(^.wrapped := props)()
    
    //when
    val result = testRender(component)
    
    //then
    assertRolePermissionPanel(result, props)
  }

  it should "return all descendant ids when getAllDescendantIds" in {
    //given
    val permissions = List(
      RolePermissionData(1, None, isNode = true, "test permission node 1", isEnabled = false),
      RolePermissionData(10, Some(1), isNode = true, "test permission node 1.0", isEnabled = false),
      RolePermissionData(11, Some(1), isNode = false, "test permission 1.1", isEnabled = false),
      RolePermissionData(12, Some(1), isNode = false, "test permission 1.2", isEnabled = true),
      RolePermissionData(2, Some(1), isNode = true, "test permission node 2", isEnabled = false),
      RolePermissionData(21, Some(2), isNode = false, "test permission 2.1", isEnabled = false),
      RolePermissionData(22, Some(2), isNode = false, "test permission 2.2", isEnabled = true)
    ).groupBy(_.parentId)
    
    //when & then
    getAllDescendantIds(-1, permissions) shouldBe Set(-1)
    getAllDescendantIds(0, permissions) shouldBe Set(0)
    getAllDescendantIds(1, permissions) shouldBe Set(1, 10, 11, 12, 2, 21, 22)
    getAllDescendantIds(2, permissions) shouldBe Set(2, 21, 22)
    getAllDescendantIds(12, permissions) shouldBe Set(12)
    getAllDescendantIds(22, permissions) shouldBe Set(22)
  }

  it should "return tree roots when buildTree" in {
    //given
    val permissions = List(
      RolePermissionData(0, None, isNode = true, "test permission node 0", isEnabled = false),
      RolePermissionData(1, None, isNode = true, "test permission node 1", isEnabled = false),
      RolePermissionData(11, Some(1), isNode = false, "test permission 1.1", isEnabled = false),
      RolePermissionData(12, Some(1), isNode = false, "test permission 1.2", isEnabled = true),
      RolePermissionData(2, Some(1), isNode = true, "test permission node 2", isEnabled = false),
      RolePermissionData(21, Some(2), isNode = false, "test permission 2.1", isEnabled = false),
      RolePermissionData(22, Some(2), isNode = false, "test permission 2.2", isEnabled = true)
    ).groupBy(_.parentId)
    
    //when & then
    buildTree(Map.empty) shouldBe Nil
    buildTree(permissions) shouldBe List(
      CheckBoxTreeNodeData("0", Deselected, "test permission node 0", None),
      CheckBoxTreeNodeData("1", Indeterminate, "test permission node 1", None, List(
        CheckBoxTreeItemData("11", Deselected, "test permission 1.1", Some(AdminImagesCss.keySmall)),
        CheckBoxTreeItemData("12", Selected, "test permission 1.2", Some(AdminImagesCss.keySmall)),
        CheckBoxTreeNodeData("2", Indeterminate, "test permission node 2", None, List(
          CheckBoxTreeItemData("21", Deselected, "test permission 2.1", Some(AdminImagesCss.keySmall)),
          CheckBoxTreeItemData("22", Selected, "test permission 2.2", Some(AdminImagesCss.keySmall))
        ))
      ))
    )
  }

  private def assertRolePermissionPanel(result: TestInstance, props: RolePermissionPanelProps): Unit = {
    val roots = buildTree(props.state.permissionsByParentId)
    
    assertTestComponent(result, checkBoxTreeComp) {
      case CheckBoxTreeProps(resultRoots, _, readOnly, openNodes, closeNodes) =>
        resultRoots shouldBe roots
        readOnly shouldBe false
        openNodes shouldBe roots.map(_.key).toSet
        closeNodes shouldBe Set.empty[String]
    }
  }
}
