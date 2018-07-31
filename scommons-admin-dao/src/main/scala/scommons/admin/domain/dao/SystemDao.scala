package scommons.admin.domain.dao

import scommons.admin.domain.{AdminDBContext, SystemEntity, SystemSchema}
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class SystemDao(val ctx: AdminDBContext)(implicit ec: ExecutionContext)
  extends CommonDao
    with SystemSchema {

  import ctx._

  def getById(id: Int): Future[Option[SystemEntity]] = {
    getOne("getById", ctx.run(systems
      .filter(c => c.id == lift(id))
    ))
  }

  def getByName(name: String): Future[Option[SystemEntity]] = {
    getOne("getByName", ctx.run(systems
      .filter(c => c.name == lift(name))
    ))
  }

  def list(): Future[List[SystemEntity]] = {
    ctx.run(systems
      .sortBy(_.name)
    )
  }

  def insert(entity: SystemEntity): Future[Int] = {
    ctx.run(systems
      .insert(lift(entity))
      .returning(_.id)
    )
  }

  def update(entity: SystemEntity): Future[Boolean] = {
    isUpdated(ctx.run(systems
      .filter(c => c.id == lift(entity.id) && c.version == lift(entity.version))
      .update(lift(entity))
    ))
  }

  def deleteAll(): Future[Unit] = {
    ctx.run(systems.delete).map(_ => ())
  }
}
