package scommons.admin.domain.dao

import scommons.admin.domain.{AdminDBContext, SystemEntity, SystemSchema, SystemUserSchema}
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class SystemDao(val ctx: AdminDBContext)
  extends CommonDao
    with SystemSchema
    with SystemUserSchema {

  import ctx._

  def getById(id: Int)(implicit ec: ExecutionContext): Future[Option[SystemEntity]] = {
    getOne("getById", ctx.run(systems
      .filter(c => c.id == lift(id))
    ))
  }

  def getByName(name: String)(implicit ec: ExecutionContext): Future[Option[SystemEntity]] = {
    getOne("getByName", ctx.run(systems
      .filter(c => c.name == lift(name))
    ))
  }

  def list()(implicit ec: ExecutionContext): Future[List[SystemEntity]] = {
    ctx.run(systems
      .sortBy(_.name)
    )
  }

  def listUserSystems(userId: Int)(implicit ec: ExecutionContext): Future[List[(SystemEntity, Boolean)]] = {
    ctx.run(systems
      .leftJoin(systemsUsers.filter(su => su.userId == lift(userId)))
      .on((s, su) => s.id == su.systemId)
      .sortBy { case (s, _) => s.name }
      .map { case (s, su) => (s, su.nonEmpty) }
    )
  }

  def insert(entity: SystemEntity)(implicit ec: ExecutionContext): Future[Int] = {
    ctx.run(systems
      .insert(lift(entity))
      .returning(_.id)
    )
  }

  def update(entity: SystemEntity)(implicit ec: ExecutionContext): Future[Boolean] = {
    isUpdated(ctx.run(systems
      .filter(c => c.id == lift(entity.id) && c.version == lift(entity.version))
      .update(lift(entity))
    ))
  }

  def deleteAll()(implicit ec: ExecutionContext): Future[Unit] = {
    ctx.run(systems.delete).map(_ => ())
  }
}
