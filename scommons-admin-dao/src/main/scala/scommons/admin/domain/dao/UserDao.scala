package scommons.admin.domain.dao

import scommons.admin.domain._
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class UserDao(val ctx: AdminDBContext)
  extends CommonDao
    with UserSchema
    with CompanySchema
    with UserProfileSchema {

  import ctx._

  def getById(id: Int)(implicit ec: ExecutionContext): Future[Option[User]] = {
    getOne("getById", ctx.run(users
      .filter(c => c.id == lift(id))
    ))
  }

  def getByLogin(login: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    getOne("getByLogin", ctx.run(users
      .filter(c => c.login == lift(login))
    ))
  }

  def getUserWithCompany(id: Int)(implicit ec: ExecutionContext): Future[Option[(User, Company)]] = {
    getOne("getUserWithCompany", ctx.run(users
      .filter(c => c.id == lift(id))
      .join(companies).on { case (user, company) => user.companyId == company.id }
    ))
  }

  def getUserDetails(id: Int)(implicit ec: ExecutionContext): Future[Option[UserDetails]] = {
    getOne("getUserDetails", ctx.run(users
      .filter(c => c.id == lift(id))
      .join(companies).on { case (user, company) => user.companyId == company.id }
      .join(usersProfiles).on { case ((user, _), profile) => user.id == profile.userId }
    )).map {
      case Some(((user, company), profile)) => Some(UserDetails(user, company, profile))
      case None => None
    }
  }

  def list(optOffset: Option[Int],
           limit: Int,
           symbols: Option[String])(implicit ec: ExecutionContext): Future[(List[(User, Company)], Option[Int])] = {

    val textLower = s"%${symbols.getOrElse("").trim.toLowerCase}%"
    val offset = optOffset.getOrElse(0)

    val futureCount = optOffset match {
      case Some(_) => Future.successful(None)
      case None => ctx.run(users
        .filter(c => c.login.toLowerCase.like(lift(textLower)))
        .size
      ).map(Some(_))
    }

    for {
      maybeCount <- futureCount
      results <- ctx.run(users
        .filter(_.login.toLowerCase.like(lift(textLower)))
        .sortBy(_.login)
        .drop(lift(offset))
        .take(lift(limit))
        .join(companies).on { case (user, company) => user.companyId == company.id }
      )
    } yield {
      (results, maybeCount.map(_.toInt))
    }
  }

  def insert(entity: User)(implicit ec: ExecutionContext): Future[Int] = {
    ctx.run(users
      .insert(lift(entity))
      .returning(_.id)
    )
  }

  def update(entity: User)(implicit ec: ExecutionContext): Future[Boolean] = {
    isUpdated(ctx.run(users
      .filter(c => c.id == lift(entity.id) && c.version == lift(entity.version))
      .update(lift(entity))
    ))
  }
}
