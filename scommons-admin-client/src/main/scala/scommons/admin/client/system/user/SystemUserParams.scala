package scommons.admin.client.system.user

case class SystemUserParams(groupId: Option[Int] = None,
                            systemId: Option[Int] = None,
                            userId: Option[Int] = None)
