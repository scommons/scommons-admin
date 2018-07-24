package scommons.admin.domain.dao

import scommons.admin.domain._
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class SystemGroupDao(val ctx: AdminDBContext)(implicit ec: ExecutionContext)
  extends CommonDao
    with SystemGroupSchema {

  import ctx._

  def getById(id: Int): Future[Option[SystemGroup]] = {
    getOne("getById", ctx.run(systemsGroups
      .filter(c => c.id == lift(id))
    ))
  }

  def getByName(name: String): Future[Option[SystemGroup]] = {
    getOne("getByName", ctx.run(systemsGroups
      .filter(c => c.name == lift(name))
    ))
  }

  def list(): Future[List[SystemGroup]] = {
    ctx.run(systemsGroups
      .sortBy(_.name)
    )
  }

  def insert(entity: SystemGroup): Future[Int] = {
    ctx.run(systemsGroups
      .insert(lift(entity))
      .returning(_.id)
    )
  }

  def update(entity: SystemGroup): Future[Boolean] = {
    isUpdated(ctx.run(systemsGroups
      .filter(c => c.id == lift(entity.id) && c.version == lift(entity.version))
      .update(lift(entity))
    ))
  }

  def deleteAll(): Future[Unit] = {
    ctx.run(systemsGroups.delete).map(_ => ())
  }
}
