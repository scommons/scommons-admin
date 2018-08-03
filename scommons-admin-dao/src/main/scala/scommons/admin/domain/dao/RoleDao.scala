package scommons.admin.domain.dao

import scommons.admin.domain.{AdminDBContext, Role, RoleSchema}
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class RoleDao(val ctx: AdminDBContext)(implicit ec: ExecutionContext)
  extends CommonDao
    with RoleSchema {

  import ctx._

  def getById(id: Int): Future[Option[Role]] = {
    getOne("getById", ctx.run(roles
      .filter(c => c.id == lift(id))
    ))
  }

  def getByTitle(systemId: Int, title: String): Future[Option[Role]] = {
    getOne("getByTitle", ctx.run(roles
      .filter(c => c.systemId == lift(systemId) && c.title == lift(title))
    ))
  }

  def getMaxBitIndex(systemId: Int): Future[Option[Int]] = {
    ctx.run(roles
      .filter(c => c.systemId == lift(systemId))
      .sortBy(_.bitIndex)(Ord.desc)
      .map(_.bitIndex)
    ).map(_.headOption)
  }

  def list(): Future[List[Role]] = {
    ctx.run(roles
      .sortBy(_.title)
    )
  }

  def insert(entity: Role): Future[Int] = {
    ctx.run(roles
      .insert(lift(entity))
      .returning(_.id)
    )
  }

  def update(entity: Role): Future[Boolean] = {
    isUpdated(ctx.run(roles
      .filter(c => c.id == lift(entity.id) && c.version == lift(entity.version))
      .update(lift(entity))
    ))
  }

  def deleteAll(): Future[Unit] = {
    ctx.run(roles.delete).map(_ => ())
  }
}
