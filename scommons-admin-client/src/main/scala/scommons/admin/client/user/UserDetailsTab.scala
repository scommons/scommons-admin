package scommons.admin.client.user

import scala.collection.mutable

class UserDetailsTab private(val id: String) extends AnyVal {
  
  override def toString: String = id
}

object UserDetailsTab {

  private val values = mutable.Map[String, UserDetailsTab]()
  
  def of(id: String): Option[UserDetailsTab] = values.get(id)

  def apply(id: String): UserDetailsTab = {
    val tab = new UserDetailsTab(id)
    UserDetailsTab.values += id -> tab
    tab
  }
  
  val systems = UserDetailsTab("systems")
  val profile = UserDetailsTab("profile")
}
