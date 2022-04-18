package scommons.admin.client.system.user

import org.joda.time.DateTime
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.system.user._
import scommons.admin.client.role.permission.RolePermissionPanel
import scommons.admin.client.system.user.SystemUserActions._
import scommons.admin.client.system.user.SystemUserRolePanel._
import scommons.client.ui.list._
import scommons.client.ui.tree._
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test._

import scala.concurrent.Future

class SystemUserRolePanelSpec extends TestSpec with TestRendererUtils {

  SystemUserRolePanel.pickListComp = mockUiComponent("PickList")
  SystemUserRolePanel.checkBoxTree = mockUiComponent("CheckBoxTree")

  //noinspection TypeAnnotation
  class Actions {
    val systemUserRolesAdd = mockFunction[Dispatch, Int, Int, SystemUserRoleUpdateReq, SystemUserRoleAddAction]
    val systemUserRolesRemove = mockFunction[Dispatch, Int, Int, SystemUserRoleUpdateReq, SystemUserRoleRemoveAction]

    val actions = new MockSystemUserActions(
      systemUserRolesAddMock = systemUserRolesAdd,
      systemUserRolesRemoveMock = systemUserRolesRemove
    )
  }

  it should "dispatch SystemUserRoleAddAction if add item(s) when onSelectChange" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val systemId = 22
    val userData = SystemUserData(
      userId = 11,
      login = "test_login_1",
      lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
      updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
      createdAt = DateTime("2018-12-03T11:29:01.234Z"),
      version = 123
    )
    val respData = SystemUserRoleRespData(
      List(SystemUserRoleData(1, "test_app", isSelected = false)), Nil, userData
    )
    val state = SystemUserState(
      selectedUser = Some(respData.systemUser),
      userRoles = respData.roles,
      permissionsByParentId = respData.permissions.groupBy(_.parentId)
    )
    val props = getSystemUserRolePanelProps(dispatch, actions.actions, data = state, systemId = systemId)
    val comp = testRender(<(SystemUserRolePanel())(^.wrapped := props)())
    val pickListProps = findComponentProps(comp, pickListComp)
    val data = SystemUserRoleUpdateReq(Set(1), userData.version)
    val action = SystemUserRoleAddAction(
      FutureTask("Test", Future.successful(SystemUserRoleResp(respData)))
    )
    actions.systemUserRolesAdd.expects(dispatch, systemId, userData.userId, data)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    pickListProps.onSelectChange(Set("1"), true)
  }

  it should "dispatch SystemUserRoleRemoveAction if remove item(s) when onSelectChange" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val systemId = 22
    val userData = SystemUserData(
      userId = 11,
      login = "test_login_1",
      lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
      updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
      createdAt = DateTime("2018-12-03T11:29:01.234Z"),
      version = 123
    )
    val respData = SystemUserRoleRespData(
      List(SystemUserRoleData(1, "test_app", isSelected = false)), Nil, userData
    )
    val state = SystemUserState(
      selectedUser = Some(respData.systemUser),
      userRoles = respData.roles,
      permissionsByParentId = respData.permissions.groupBy(_.parentId)
    )
    val props = getSystemUserRolePanelProps(dispatch, actions.actions, data = state, systemId = systemId)
    val comp = testRender(<(SystemUserRolePanel())(^.wrapped := props)())
    val pickListProps = findComponentProps(comp, pickListComp)
    val data = SystemUserRoleUpdateReq(Set(1), userData.version)
    val action = SystemUserRoleRemoveAction(
      FutureTask("Test", Future.successful(SystemUserRoleResp(respData)))
    )
    actions.systemUserRolesRemove.expects(dispatch, systemId, userData.userId, data)
      .returning(action)

    //then
    dispatch.expects(action)

    //when
    pickListProps.onSelectChange(Set("1"), false)
  }

  it should "render component" in {
    //given
    val props = getSystemUserRolePanelProps()
    val component = <(SystemUserRolePanel())(^.wrapped := props)()
    
    //when
    val result = testRender(component)
    
    //then
    assertSystemUserRolePanel(result, props)
  }

  private def getSystemUserState: SystemUserState = {
    val user = SystemUserData(
      userId = 1,
      login = "test_login_1",
      lastLoginDate = Some(DateTime("2018-12-04T15:29:01.234Z")),
      updatedAt = DateTime("2018-12-03T10:29:01.234Z"),
      createdAt = DateTime("2018-12-03T11:29:01.234Z"),
      version = 123
    )

    SystemUserState(
      dataList = List(
        user,
        SystemUserData(
          userId = 2,
          login = "test_login_2",
          lastLoginDate = None,
          updatedAt = DateTime("2018-12-01T10:29:01.234Z"),
          createdAt = DateTime("2018-12-02T11:29:01.234Z"),
          version = 124
        )
      )
    )
  }

  private def getSystemUserRolePanelProps(dispatch: Dispatch = mockFunction[Any, Any],
                                          actions: SystemUserActions = mock[SystemUserActions],
                                          data: SystemUserState = getSystemUserState,
                                          systemId: Int = 12): SystemUserRolePanelProps = {

    SystemUserRolePanelProps(
      dispatch = dispatch,
      actions = actions,
      data = data,
      systemId = systemId
    )
  }

  private def assertSystemUserRolePanel(result: TestInstance, props: SystemUserRolePanelProps): Unit = {
    val roots = RolePermissionPanel.buildTree(props.data.permissionsByParentId)

    assertNativeComponent(result, <.div(^.className := "row-fluid")(), inside(_) { case List(col1, col2) =>
      assertNativeComponent(col1, <.div(^.className := "span6")(), inside(_) { case List(pickList) =>
        assertTestComponent(pickList, pickListComp) {
          case PickListProps(items, selectedIds, preSelectedIds, _, sourceTitle, destTitle) =>
            items shouldBe props.data.userRoles.map { r =>
              ListBoxData(r.id.toString, r.title, Some(AdminImagesCss.role))
            }
            selectedIds shouldBe props.data.userRoles.filter(_.isSelected).map(_.id.toString).toSet
            preSelectedIds shouldBe Set.empty[String]
            sourceTitle shouldBe "Available Roles"
            destTitle shouldBe "Assigned Roles"
        }
      })
      assertNativeComponent(col2, <.div(^.className := "span6")(), inside(_) { case List(resCheckBoxTree) =>
        assertTestComponent(resCheckBoxTree, checkBoxTree) {
          case CheckBoxTreeProps(resultRoots, _, readOnly, openNodes, closeNodes) =>
            resultRoots shouldBe roots
            readOnly shouldBe true
            openNodes shouldBe roots.map(_.key).toSet
            closeNodes shouldBe Set.empty[String]
        }
      })
    })
  }
}
