package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.elements.ReactElement
import org.scalatest._
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.user._
import scommons.client.ui.tab._
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowRenderer.ComponentInstance
import scommons.react.test.util.ShallowRendererUtils

class UserDetailsPanelSpec extends TestSpec with ShallowRendererUtils {
  
  it should "call onChangeTab when select tab" in {
    //given
    val onChangeTab = mockFunction[Option[UserDetailsTab], Unit]
    val props = getUserDetailsPanelProps(onChangeTab = onChangeTab)
    val comp = shallowRender(<(UserDetailsPanel())(^.wrapped := props)())
    val tabPanelProps = findComponentProps(comp, TabPanel)
    tabPanelProps.selectedIndex shouldBe 0
    
    //then
    onChangeTab.expects(Some(UserDetailsTab.profile))
    
    //when
    tabPanelProps.onSelect(null, 1)
  }

  it should "render component" in {
    //given
    val props = getUserDetailsPanelProps()
    val component = <(UserDetailsPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserDetailsPanel(result, props)
  }

  it should "render component with selected Applications tab" in {
    //given
    val props = getUserDetailsPanelProps(selectedTab = Some(UserDetailsTab.apps))
    val component = <(UserDetailsPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserDetailsPanel(result, props)
  }

  it should "render component with selected Profile tab" in {
    //given
    val props = getUserDetailsPanelProps(selectedTab = Some(UserDetailsTab.profile))
    val component = <(UserDetailsPanel())(^.wrapped := props)()
    
    //when
    val result = shallowRender(component)
    
    //then
    assertUserDetailsPanel(result, props)
  }

  private def getUserDetailsPanelProps(renderSystems: Props[_] => ReactElement = _ => <.div.empty,
                                       profile: UserProfileData = UserProfileData(
                                         email = "test@email.com",
                                         firstName = "Firstname",
                                         lastName = "Lastname",
                                         phone = Some("0123 456 789")
                                       ),
                                       selectedTab: Option[UserDetailsTab] = None,
                                       onChangeTab: Option[UserDetailsTab] => Unit = _ => ()
                                      ): UserDetailsPanelProps = {
    
    UserDetailsPanelProps(
      renderSystems = renderSystems,
      profile = profile,
      selectedTab = selectedTab,
      onChangeTab = onChangeTab
    )
  }

  private def assertUserDetailsPanel(result: ComponentInstance, props: UserDetailsPanelProps): Unit = {
    
    def assertUserProfilePanel(component: ReactElement, data: UserProfileData): Assertion = {
      val wrapped = React.createClass[Unit, Unit] { _ =>
        <.div()(component)
      }
      val result = shallowRender(<(wrapped)()())

      assertNativeComponent(result, <.div()(), { case List(comp) =>
        assertComponent(comp, UserProfilePanel) {
          case UserProfilePanelProps(resultData) =>
            resultData shouldBe data
        }
      })
    }

    assertComponent(result, TabPanel) {
      case TabPanelProps(List(systemsItem, profileItem), selectedIndex, _, direction) =>
        selectedIndex shouldBe props.selectedTab.map {
          case UserDetailsTab.`apps` => 0
          case UserDetailsTab.profile => 1
        }.getOrElse(0)
        direction shouldBe TabDirection.Top

        inside(systemsItem) { case TabItemData(title, image, component, render) =>
          title shouldBe "Applications"
          image shouldBe Some(AdminImagesCss.computer)
          component shouldBe None
          render shouldBe Some(props.renderSystems)
        }
        
        inside(profileItem) { case TabItemData(title, image, component, render) =>
          title shouldBe "Profile"
          image shouldBe Some(AdminImagesCss.vcard)
          component shouldBe None
          render should not be None

          assertUserProfilePanel(render.get.apply(null), props.profile)
        }
    }
  }
}
