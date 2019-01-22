package scommons.admin.domain.dao

import scommons.admin.domain.{AdminDBContext, RolePermission, RolePermissionSchema}
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class RolePermissionDao(val ctx: AdminDBContext)
  extends CommonDao
    with RolePermissionSchema {

  import ctx._

  def insert(entities: Set[RolePermission])(implicit ec: ExecutionContext): Future[Unit] = {
    ctx.run(liftQuery(entities)
      .foreach(c => rolesPermissions.insert(c))
    ).map(_ => ())
  }

  def delete(roleId: Int, permissionIds: Set[Int])(implicit ec: ExecutionContext): Future[Boolean] = {
    isUpdated(ctx.run(rolesPermissions
      .filter(c => c.roleId == lift(roleId)
        && liftQuery(permissionIds).contains(c.permissionId))
      .delete
    ))
  }
}
