package scommons.admin.domain.dao

import scommons.admin.domain._
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class PermissionDao(val ctx: AdminDBContext)
  extends CommonDao
    with PermissionSchema
    with RolePermissionSchema {

  import ctx._

  def list(systemId: Int, roleId: Option[Int])(implicit ec: ExecutionContext): Future[List[(Permission, Boolean)]] = {
    ctx.run(permissions.filter(p => p.systemId == lift(systemId))
      .leftJoin(rolesPermissions.filter(rp => rp.roleId == lift(roleId.getOrElse(0))))
      .on((p, rp) => p.id == rp.permissionId)
      .sortBy { case (p, _) => (p.parentId, p.title) }
      .map { case (p, rp) => (p, rp.nonEmpty) }
    )
  }

  def insert(entities: Set[Permission])(implicit ec: ExecutionContext): Future[Unit] = {
    ctx.run(liftQuery(entities)
      .foreach(c => permissions.insert(c))
    ).map(_ => ())
  }
}
