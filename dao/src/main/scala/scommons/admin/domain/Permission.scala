package scommons.admin.domain

case class Permission(id: Int,
                      systemId: Int,
                      name: String,
                      title: String,
                      isNode: Boolean,
                      parentId: Option[Int])

//noinspection TypeAnnotation
trait PermissionSchema {

  val ctx: AdminDBContext
  import ctx._

  implicit val permissionsInsertMeta = insertMeta[Permission](
    _.id
  )
  implicit val permissionsUpdateMeta = updateMeta[Permission](
    _.id
  )

  val permissions = quote {
    querySchema[Permission](
      "permissions",
      _.id -> "id",
      _.systemId -> "system_id",
      _.name -> "name",
      _.title -> "title",
      _.isNode -> "is_node",
      _.parentId -> "parent_id"
    )
  }
}
