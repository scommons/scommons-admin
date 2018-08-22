package services

import scommons.admin.domain.dao.{PermissionDao, RoleDao, RolePermissionDao}
import scommons.admin.domain.{Permission, Role, RolePermission}

import scala.concurrent.{ExecutionContext, Future}

class RolePermissionService(roleDao: RoleDao,
                            permissionDao: PermissionDao,
                            rolePermissionDao: RolePermissionDao)(implicit ec: ExecutionContext) {

  import rolePermissionDao.ctx

  def listRolePermissions(role: Role): Future[List[(Permission, Boolean)]] = {
    permissionDao.list(role.systemId, Some(role.id))
  }
  
  def addRolePermissions(role: Role, permissionIds: Set[Int]): Future[Option[List[(Permission, Boolean)]]] = {
    ctx.transaction { implicit ec: ExecutionContext =>
      for {
        currPermissions <- permissionDao.list(role.systemId, Some(role.id))
        rolePermissions = currPermissions.collect {
          case (p, false) if !p.isNode && permissionIds.contains(p.id) =>
            RolePermission(role.id, p.id)
        }
        updated <- roleDao.update(role)
        _ <- rolePermissionDao.insert(rolePermissions.toSet)
        res <-
          if (updated) permissionDao.list(role.systemId, Some(role.id)).map(Some(_))
          else {
            //rollback transaction
            throw new IllegalStateException("Role was already updated")
          }
      } yield {
        res
      }
    }.recover {
      case _: IllegalStateException => None
    }
  }
  
  def removeRolePermissions(role: Role, permissionIds: Set[Int]): Future[Option[List[(Permission, Boolean)]]] = {
    ctx.transaction { implicit ec: ExecutionContext =>
      for {
        updated <- roleDao.update(role)
        _ <- rolePermissionDao.delete(role.id, permissionIds)
        res <-
          if (updated) permissionDao.list(role.systemId, Some(role.id)).map(Some(_))
          else {
            //rollback transaction
            throw new IllegalStateException("Role was already updated")
          }
      } yield {
        res
      }
    }.recover {
      case _: IllegalStateException => None
    }
  }
}
