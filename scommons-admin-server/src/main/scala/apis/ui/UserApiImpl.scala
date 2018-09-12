package apis.ui

import apis.ui.UserApiImpl._
import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.user._
import scommons.admin.domain.{Company, User, UserDetails, UserProfile}
import scommons.service.util.HashUtils
import services.{CompanyService, UserService}

import scala.concurrent.{ExecutionContext, Future}

class UserApiImpl(userService: UserService,
                  companyService: CompanyService
                 )(implicit ec: ExecutionContext) extends UserApi {

  private val defaultLimit = 10

  def getUserById(id: Int): Future[UserDetailsResp] = {
    userService.getUserById(id).map {
      case None => UserDetailsResp(UserNotFound)
      case Some((details)) => UserDetailsResp(convertToUserDetailsData(details))
    }
  }

  def listUsers(offset: Option[Int],
                limit: Option[Int],
                symbols: Option[String]): Future[UserListResp] = {
    
    userService.listUsers(offset, limit.getOrElse(defaultLimit), symbols).map {
      case (list, totalCount) =>
        UserListResp(list.map { case (user, company) =>
          convertToUserData(user, company)
        }, totalCount)
    }
  }

  def createUser(data: UserDetailsData): Future[UserDetailsResp] = {
    validateUserData(false, data, { case (_, entity) =>
      userService.createUser(entity).map { details =>
        UserDetailsResp(convertToUserDetailsData(details))
      }
    })
  }

  def updateUser(data: UserDetailsData): Future[UserDetailsResp] = {
    validateUserData(true, data, {
      case (Some(curr), entity) if curr.user != entity.user && curr.profile != entity.profile =>
        userService.updateUserDetails(entity).map {
          case None => UserDetailsResp(UserAlreadyUpdated)
          case Some(details) => UserDetailsResp(convertToUserDetailsData(details))
        }
      case (Some(curr), entity) if curr.user != entity.user =>
        userService.updateUser(entity.user).map {
          case None => UserDetailsResp(UserAlreadyUpdated)
          case Some(details) => UserDetailsResp(convertToUserDetailsData(details))
        }
      case (Some(curr), entity) if curr.profile != entity.profile =>
        userService.updateUserProfile(entity.profile).map {
          case None => UserDetailsResp(UserAlreadyUpdated)
          case Some(details) => UserDetailsResp(convertToUserDetailsData(details))
        }
      case _ => Future.successful(UserDetailsResp(data))
    })
  }

  private def validateUserData(update: Boolean,
                               data: UserDetailsData,
                               onSuccess: (Option[UserDetails], UserDetails) => Future[UserDetailsResp]
                              ): Future[UserDetailsResp] = {

    val entity = convertToUserDetails(data)
    if (entity.user.login.isEmpty) {
      throw new IllegalArgumentException("login is blank")
    }
    if (entity.profile.email.isEmpty) {
      throw new IllegalArgumentException("email is blank")
    }

    def getById(data: UserDetailsData) = data.user.id match {
      case Some(id) if update => userService.getUserById(id)
      case _ => Future.successful(None)
    }

    def getByLogin(current: Option[UserDetails], entity: UserDetails): Future[Option[User]] = {
      current match {
        case Some(curr) if curr.user.login == entity.user.login => Future.successful(None)
        case _ => userService.getUserByLogin(entity.user.login)
      }
    }

    def getByEmail(current: Option[UserDetails], entity: UserDetails): Future[Option[UserProfile]] = {
      current match {
        case Some(curr) if curr.profile.email == entity.profile.email => Future.successful(None)
        case _ => userService.getUserProfileByEmail(entity.profile.email)
      }
    }

    def checkCompanyExists(current: Option[UserDetails], entity: UserDetails): Future[Boolean] = {
      current match {
        case Some(curr) if curr.user.companyId == entity.user.companyId => Future.successful(true)
        case _ => companyService.getCompanyById(entity.user.companyId).map(_.nonEmpty)
      }
    }

    getById(data).flatMap { current =>
      if (current.isEmpty && update) Future.successful(UserDetailsResp(UserNotFound))
      else {
        Future.sequence(List(
          getByLogin(current, entity),
          getByEmail(current, entity),
          checkCompanyExists(current, entity)
        )).flatMap {
          case List(Some(_), _, _) => Future.successful(UserDetailsResp(UserLoginAlreadyExists))
          case List(_, Some(_), _) => Future.successful(UserDetailsResp(UserEmailAlreadyExists))
          case List(_, _, false) => Future.successful(UserDetailsResp(CompanyNotFound))
          case List(None, None, true) => current match {
            case None => onSuccess(current, entity)
            case Some(curr) => onSuccess(current, entity.copy(
              user = entity.user.copy(
                id = curr.user.id,
                passhash =
                  if (entity.user.passhash == passwordPlaceholder) curr.user.passhash
                  else entity.user.passhash,

                updatedAt = curr.user.updatedAt,
                createdAt = curr.user.createdAt,
                
                //DON'T UPDATE READ-ONLY FIELDS !!!
                lastLoginDate = curr.user.lastLoginDate
              ),
              profile = entity.profile.copy(
                userId = curr.user.id,
                updatedAt = curr.profile.updatedAt,
                createdAt = curr.profile.createdAt
              )
            ))
          }
        }
      }
    }
  }
}

object UserApiImpl {

  private val passwordPlaceholder = "*****"

  def convertToUserDetailsData(details: UserDetails): UserDetailsData = {
    UserDetailsData(
      user = convertToUserData(details.user, details.company),
      profile = convertToUserProfileData(details.profile)
    )
  }

  def convertToUserData(user: User, company: Company): UserData = {
    UserData(
      id = Some(user.id),
      company = UserCompanyData(company.id, company.name),
      login = user.login,
      password = passwordPlaceholder,
      active = user.active,
      lastLoginDate = user.lastLoginDate,
      updatedAt = Some(user.updatedAt),
      createdAt = Some(user.createdAt),
      version = Some(user.version)
    )
  }
  
  def convertToUserProfileData(profile: UserProfile): UserProfileData = UserProfileData(
    email = profile.email,
    firstName = profile.firstName,
    lastName = profile.lastName,
    phone = profile.phone,
    updatedAt = Some(profile.updatedAt),
    createdAt = Some(profile.createdAt),
    version = Some(profile.version)
  )

  def convertToUserDetails(data: UserDetailsData): UserDetails = UserDetails(
    user = convertToUser(data.user),
    company = Company(data.user.company.id, data.user.company.name),
    profile = convertToUserProfile(data.profile)
  )
  
  def convertToUser(data: UserData): User = User(
    id = data.id.getOrElse(-1),
    companyId = data.company.id,
    login = data.login.trim,
    passhash =
      if (data.password == passwordPlaceholder) passwordPlaceholder
      else HashUtils.sha1(data.password),
    active = data.active,
    lastLoginDate = None,
    updatedBy = Some(1), //TODO: use current userId (from request)
    version = data.version.getOrElse(-1)
  )

  def convertToUserProfile(profile: UserProfileData): UserProfile = UserProfile(
    userId = -1,
    email = profile.email.trim,
    firstName = profile.firstName.trim,
    lastName = profile.lastName.trim,
    phone = profile.phone.map(_.trim),
    updatedBy = 1, //TODO: use current userId (from request)
    version = profile.version.getOrElse(-1)
  )
}
