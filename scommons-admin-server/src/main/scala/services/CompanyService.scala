package services

import scommons.admin.domain.Company
import scommons.admin.domain.dao.CompanyDao

import scala.concurrent.{ExecutionContext, Future}

class CompanyService(companyDao: CompanyDao)(implicit ec: ExecutionContext) {

  import companyDao.ctx

  def getCompanyById(id: Int): Future[Option[Company]] = {
    companyDao.getById(id)
  }

  def getCompanyByName(name: String): Future[Option[Company]] = {
    companyDao.getByName(name)
  }

  def listCompanies(offset: Option[Int],
                    limit: Int,
                    symbols: Option[String]): Future[(List[Company], Option[Int])] = {

    companyDao.list(offset, limit, symbols)
  }

  def createCompany(entity: Company): Future[Company] = {
    ctx.transaction { implicit ec =>
      companyDao.insert(entity).flatMap { id =>
        companyDao.getById(id).map(_.get)
      }
    }
  }
  
  def updateCompany(entity: Company): Future[Option[Company]] = {
    ctx.transaction { implicit ec =>
      companyDao.update(entity).flatMap {
        case false => Future.successful(None)
        case true => companyDao.getById(entity.id)
      }
    }
  }
}
