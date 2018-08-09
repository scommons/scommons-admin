package scommons.admin.domain.dao

import scommons.admin.domain.{AdminDBContext, Role, RoleSchema}
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class RoleDao(val ctx: AdminDBContext)
  extends CommonDao
    with RoleSchema {

  import ctx._

  def getById(id: Int)(implicit ec: ExecutionContext): Future[Option[Role]] = {
    getOne("getById", ctx.run(roles
      .filter(c => c.id == lift(id))
    ))
  }

  def getByTitle(systemId: Int, title: String)(implicit ec: ExecutionContext): Future[Option[Role]] = {
    getOne("getByTitle", ctx.run(roles
      .filter(c => c.systemId == lift(systemId) && c.title == lift(title))
    ))
  }

  def getMaxBitIndex(systemId: Int)(implicit ec: ExecutionContext): Future[Option[Int]] = {
    ctx.run(roles
      .filter(c => c.systemId == lift(systemId))
      .map(_.bitIndex)
      .max
    )
  }

  def list()(implicit ec: ExecutionContext): Future[List[Role]] = {
    ctx.run(roles
      .sortBy(_.title)
    )
  }

  def insert(entity: Role)(implicit ec: ExecutionContext): Future[Int] = {
    ctx.run(roles
      .insert(lift(entity))
      .returning(_.id)
    )
  }

  def update(entity: Role)(implicit ec: ExecutionContext): Future[Boolean] = {
    isUpdated(ctx.run(roles
      .filter(c => c.id == lift(entity.id) && c.version == lift(entity.version))
      .update(lift(entity))
    ))
  }

  def deleteAll()(implicit ec: ExecutionContext): Future[Unit] = {
    ctx.run(roles.delete).map(_ => ())
  }
}
