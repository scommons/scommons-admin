package scommons.admin.server.company

import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.company._
import scommons.admin.domain.Company
import scommons.admin.server.company.CompanyApiImpl._

import scala.concurrent.{ExecutionContext, Future}

class CompanyApiImpl(service: CompanyService)(implicit ec: ExecutionContext)
  extends CompanyApi {

  private val defaultLimit = 10

  def getCompanyById(id: Int): Future[CompanyResp] = {
    service.getCompanyById(id).map {
      case None => CompanyResp(CompanyNotFound)
      case Some(company) => CompanyResp(convertToCompanyData(company))
    }
  }
  
  def listCompanies(offset: Option[Int],
                    limit: Option[Int],
                    symbols: Option[String]): Future[CompanyListResp] = {
    
    service.listCompanies(offset, limit.getOrElse(defaultLimit), symbols).map {
      case (list, totalCount) =>
        CompanyListResp(list.map(convertToCompanyData), totalCount)
    }
  }

  def createCompany(data: CompanyData): Future[CompanyResp] = {
    validateCompanyData(false, data, { entity =>
      service.createCompany(entity).map { repo =>
        CompanyResp(convertToCompanyData(repo))
      }
    })
  }

  def updateCompany(data: CompanyData): Future[CompanyResp] = {
    validateCompanyData(true, data, { entity =>
      service.updateCompany(entity).map {
        case None => CompanyResp(CompanyNotFound)
        case Some(company) => CompanyResp(convertToCompanyData(company))
      }
    })
  }

  private def validateCompanyData(update: Boolean,
                                  data: CompanyData,
                                  onSuccess: Company => Future[CompanyResp]): Future[CompanyResp] = {

    val entity = convertToCompany(data)
    if (entity.name.isEmpty) {
      throw new IllegalArgumentException("name is blank")
    }

    def getById(data: CompanyData) = data.id match {
      case Some(id) if update => service.getCompanyById(id)
      case _ => Future.successful(None)
    }

    def getByName(current: Option[Company], entity: Company): Future[Option[Company]] = current match {
      case Some(curr) if curr.name == entity.name => Future.successful(None)
      case _ => service.getCompanyByName(entity.name)
    }

    getById(data).flatMap { current =>
      if (current.isEmpty && update) Future.successful(CompanyResp(CompanyNotFound))
      else {
        getByName(current, entity).flatMap {
          case Some(_) => Future.successful(CompanyResp(CompanyAlreadyExists))
          case None => onSuccess(entity)
        }
      }
    }
  }
}

object CompanyApiImpl {

  def convertToCompanyData(c: Company): CompanyData = CompanyData(
    Some(c.id),
    c.name
  )

  def convertToCompany(data: CompanyData): Company = Company(
    data.id.getOrElse(-1),
    data.name.trim
  )
}
