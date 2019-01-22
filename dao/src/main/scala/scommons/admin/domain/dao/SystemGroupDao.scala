package scommons.admin.domain.dao

import scommons.admin.domain.{AdminDBContext, SystemGroup, SystemGroupSchema}
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class SystemGroupDao(val ctx: AdminDBContext)
  extends CommonDao
    with SystemGroupSchema {

  import ctx._

  def getById(id: Int)(implicit ec: ExecutionContext): Future[Option[SystemGroup]] = {
    getOne("getById", ctx.run(systemsGroups
      .filter(c => c.id == lift(id))
    ))
  }

  def getByName(name: String)(implicit ec: ExecutionContext): Future[Option[SystemGroup]] = {
    getOne("getByName", ctx.run(systemsGroups
      .filter(c => c.name == lift(name))
    ))
  }

  def list()(implicit ec: ExecutionContext): Future[List[SystemGroup]] = {
    ctx.run(systemsGroups
      .sortBy(_.name)
    )
  }

  def insert(entity: SystemGroup)(implicit ec: ExecutionContext): Future[Int] = {
    ctx.run(systemsGroups
      .insert(lift(entity))
      .returning(_.id)
    )
  }

  def update(entity: SystemGroup)(implicit ec: ExecutionContext): Future[Boolean] = {
    isUpdated(ctx.run(systemsGroups
      .filter(c => c.id == lift(entity.id) && c.version == lift(entity.version))
      .update(lift(entity))
    ))
  }

  def deleteAll()(implicit ec: ExecutionContext): Future[Unit] = {
    ctx.run(systemsGroups.delete).map(_ => ())
  }
}
