package scommons.admin.domain.dao

import scommons.admin.domain.{AdminDBContext, UserProfile, UserProfileSchema}
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class UserProfileDao(val ctx: AdminDBContext)
  extends CommonDao
    with UserProfileSchema {

  import ctx._

  def getByUserId(userId: Int)(implicit ec: ExecutionContext): Future[Option[UserProfile]] = {
    getOne("getByUserId", ctx.run(usersProfiles
      .filter(c => c.userId == lift(userId))
    ))
  }

  def getByEmail(email: String)(implicit ec: ExecutionContext): Future[Option[UserProfile]] = {
    getOne("getByEmail", ctx.run(usersProfiles
      .filter(c => c.email == lift(email))
    ))
  }

  def insert(entity: UserProfile)(implicit ec: ExecutionContext): Future[Unit] = {
    ctx.run(usersProfiles
      .insert(lift(entity))
    ).map(_ => ())
  }

  def update(entity: UserProfile)(implicit ec: ExecutionContext): Future[Boolean] = {
    isUpdated(ctx.run(usersProfiles
      .filter(c => c.userId == lift(entity.userId) && c.version == lift(entity.version))
      .update(lift(entity))
    ))
  }
}
