package scommons.admin.client.user

case class UserParams(userId: Option[Int] = None,
                      tab: Option[UserDetailsTab] = None)
