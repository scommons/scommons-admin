package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.user._
import scommons.client.ui._
import scommons.client.ui.tab.{TabItemData, TabPanel, TabPanelProps}

case class UserDetailsPanelProps(profile: UserProfileData,
                                 selectedTab: Option[UserDetailsTab],
                                 onChangeTab: Option[UserDetailsTab] => Unit)

object UserDetailsPanel extends UiComponent[UserDetailsPanelProps] {

  def apply(): ReactClass = reactClass
  lazy val reactClass: ReactClass = createComp

  private def createComp = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped
    
    val tabItems = List(
      UserDetailsTab.systems -> TabItemData("Systems", image = Some(AdminImagesCss.computer), render = Some { _ =>
        <.div()("User Systems")
      }),
      UserDetailsTab.profile -> TabItemData("Profile", image = Some(AdminImagesCss.vcard), render = Some { _ =>
        <(UserProfilePanel())(^.wrapped := UserProfilePanelProps(props.profile))()
      })
    )

    <(TabPanel())(^.wrapped := TabPanelProps(
      items = tabItems.map(_._2),
      selectedIndex = props.selectedTab.map { t =>
        tabItems.prefixLength { case (tab, _) => t != tab }
      }.getOrElse(0),
      onSelect = { (_, index) =>
        props.onChangeTab(Some(tabItems(index)._1))
        true
      }
    ))()
  }
}
