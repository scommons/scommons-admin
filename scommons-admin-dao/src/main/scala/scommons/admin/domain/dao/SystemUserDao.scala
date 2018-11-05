package scommons.admin.domain.dao

import scommons.admin.domain.{AdminDBContext, SystemUser, SystemUserSchema}
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class SystemUserDao(val ctx: AdminDBContext)
  extends CommonDao
    with SystemUserSchema {

  import ctx._

  def insert(entities: List[SystemUser])(implicit ec: ExecutionContext): Future[Unit] = {
    ctx.run(liftQuery(entities)
      .foreach(c => systemsUsers.insert(c))
    ).map(_ => ())
  }

  def delete(userId: Int, systemIds: Set[Int])(implicit ec: ExecutionContext): Future[Boolean] = {
    isUpdated(ctx.run(systemsUsers
      .filter(c => c.userId == lift(userId)
        && liftQuery(systemIds).contains(c.systemId))
      .delete
    ))
  }
}
