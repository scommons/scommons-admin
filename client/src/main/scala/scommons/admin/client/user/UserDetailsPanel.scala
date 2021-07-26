package scommons.admin.client.user

import io.github.shogowada.scalajs.reactjs.React.Props
import scommons.admin.client.AdminImagesCss
import scommons.admin.client.api.user._
import scommons.client.ui.tab._
import scommons.react._

case class UserDetailsPanelProps(renderSystems: Props[_] => ReactElement,
                                 profile: UserProfileData,
                                 selectedTab: Option[UserDetailsTab],
                                 onChangeTab: Option[UserDetailsTab] => Unit)

object UserDetailsPanel extends FunctionComponent[UserDetailsPanelProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    
    val tabItems = List(
      UserDetailsTab.apps -> TabItemData("Applications", image = Some(AdminImagesCss.computer), render = Some(
        props.renderSystems
      )),
      UserDetailsTab.profile -> TabItemData("Profile", image = Some(AdminImagesCss.vcard), render = Some { _ =>
        <(UserProfilePanel())(^.wrapped := UserProfilePanelProps(props.profile))()
      })
    )

    <(TabPanel())(^.wrapped := TabPanelProps(
      items = tabItems.map(_._2),
      selectedIndex = props.selectedTab.map { t =>
        tabItems.segmentLength({ case (tab, _) => t != tab }, from = 0)
      }.getOrElse(0),
      onSelect = { (_, index) =>
        props.onChangeTab(Some(tabItems(index)._1))
      }
    ))()
  }
}
