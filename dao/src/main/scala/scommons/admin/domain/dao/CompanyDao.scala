package scommons.admin.domain.dao

import scommons.admin.domain.{AdminDBContext, Company, CompanySchema}
import scommons.service.dao.CommonDao

import scala.concurrent.{ExecutionContext, Future}

class CompanyDao(val ctx: AdminDBContext)
  extends CommonDao
    with CompanySchema {

  import ctx._

  def getById(id: Int)(implicit ec: ExecutionContext): Future[Option[Company]] = {
    getOne("getById", ctx.run(companies
      .filter(c => c.id == lift(id))
    ))
  }

  def getByName(name: String)(implicit ec: ExecutionContext): Future[Option[Company]] = {
    getOne("getByName", ctx.run(companies
      .filter(c => c.name == lift(name))
    ))
  }

  def list(optOffset: Option[Int],
           limit: Int,
           symbols: Option[String])(implicit ec: ExecutionContext): Future[(List[Company], Option[Int])] = {

    val textLower = s"%${symbols.getOrElse("").trim.toLowerCase}%"
    val offset = optOffset.getOrElse(0)

    val futureCount = optOffset match {
      case Some(_) => Future.successful(None)
      case None => ctx.run(companies
        .filter(c => c.name.toLowerCase.like(lift(textLower)))
        .size
      ).map(Some(_))
    }

    for {
      maybeCount <- futureCount
      results <- ctx.run(companies
        .filter(_.name.toLowerCase.like(lift(textLower)))
        .sortBy(_.name)
        .drop(lift(offset))
        .take(lift(limit))
      )
    } yield {
      (results, maybeCount.map(_.toInt))
    }
  }

  def insert(entity: Company)(implicit ec: ExecutionContext): Future[Int] = {
    ctx.run(companies
      .insert(lift(entity))
      .returning(_.id)
    )
  }

  def update(entity: Company)(implicit ec: ExecutionContext): Future[Boolean] = {
    isUpdated(ctx.run(companies
      .filter(c => c.id == lift(entity.id))
      .update(lift(entity))
    ))
  }

  def deleteAll()(implicit ec: ExecutionContext): Future[Unit] = {
    ctx.run(companies.delete).map(_ => ())
  }
}
