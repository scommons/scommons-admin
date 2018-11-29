package scommons.admin.domain.dao

import scommons.admin.domain._
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class SystemUserDao(val ctx: AdminDBContext)
  extends CommonDao
    with SystemUserSchema
    with UserSchema {

  import ctx._

  def getBySystemId(systemId: Int)(implicit ec: ExecutionContext): Future[List[SystemUser]] = {
    ctx.run(systemsUsers
      .filter(c => c.systemId == lift(systemId))
    )
  }

  def list(systemId: Int,
           optOffset: Option[Int],
           limit: Int,
           symbols: Option[String])(implicit ec: ExecutionContext): Future[(List[(SystemUser, User)], Option[Int])] = {

    val textLower = s"%${symbols.getOrElse("").trim.toLowerCase}%"
    val offset = optOffset.getOrElse(0)

    val futureCount = optOffset match {
      case Some(_) => Future.successful(None)
      case None => ctx.run(systemsUsers
        .filter(c => c.systemId == lift(systemId))
        .join(users.filter(_.login.toLowerCase.like(lift(textLower)))).on { case (su, user) => su.userId == user.id }
        .size
      ).map(Some(_))
    }

    for {
      maybeCount <- futureCount
      results <- ctx.run(systemsUsers
        .filter(c => c.systemId == lift(systemId))
        .join(users.filter(_.login.toLowerCase.like(lift(textLower)))).on { case (su, user) => su.userId == user.id }
        .sortBy(_._2.login)
        .drop(lift(offset))
        .take(lift(limit))
      )
    } yield {
      (results, maybeCount.map(_.toInt))
    }
  }

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
