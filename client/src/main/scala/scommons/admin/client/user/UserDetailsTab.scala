package scommons.admin.client.user

import scala.collection.mutable

sealed abstract class UserDetailsTab private(val id: String) {

  UserDetailsTab.values += id -> this
  
  override def toString: String = id
}

object UserDetailsTab {

  private val values = mutable.Map[String, UserDetailsTab]()
  
  def of(id: String): Option[UserDetailsTab] = values.get(id)

  case object apps extends UserDetailsTab("apps")
  case object profile extends UserDetailsTab("profile")
}
