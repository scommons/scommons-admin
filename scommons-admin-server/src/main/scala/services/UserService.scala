package services

import scommons.admin.domain.dao.{UserDao, UserProfileDao}
import scommons.admin.domain.{Company, User, UserDetails, UserProfile}

import scala.concurrent.{ExecutionContext, Future}

class UserService(userDao: UserDao,
                  userProfileDao: UserProfileDao)(implicit ec: ExecutionContext) {

  import userDao.ctx

  def getUserById(id: Int): Future[Option[UserDetails]] = {
    userDao.getUserDetails(id)
  }

  def getUserByLogin(login: String): Future[Option[User]] = {
    userDao.getByLogin(login)
  }

  def getUserProfileByEmail(email: String): Future[Option[UserProfile]] = {
    userProfileDao.getByEmail(email)
  }

  def listUsers(offset: Option[Int],
                limit: Int,
                symbols: Option[String]): Future[(List[(User, Company)], Option[Int])] = {

    userDao.list(offset, limit, symbols)
  }

  def createUser(details: UserDetails): Future[UserDetails] = {
    ctx.transaction { implicit ec =>
      for {
        id <- userDao.insert(details.user)
        _ <- userProfileDao.insert(details.profile.copy(userId = id))
        res <- userDao.getUserDetails(id)
      } yield {
        res.get
      }
    }
  }
  
  def updateUserDetails(details: UserDetails): Future[Option[UserDetails]] = {
    ctx.transaction { implicit ec =>
      for {
        isUserUpdated <- userDao.update(details.user)
        isProfileUpdated <- userProfileDao.update(details.profile)
        res <- if (isUserUpdated && isProfileUpdated) {
          userDao.getUserDetails(details.user.id)
        }
        else {
          //rollback transaction
          throw new IllegalStateException("User was already updated")
        }
      } yield {
        res
      }
    }.recover {
      case _: IllegalStateException => None
    }
  }
  
  def updateUser(user: User): Future[Option[UserDetails]] = {
    ctx.transaction { implicit ec =>
      userDao.update(user).flatMap {
        case false => Future.successful(None)
        case true => userDao.getUserDetails(user.id)
      }
    }
  }
  
  def updateUserProfile(profile: UserProfile): Future[Option[UserDetails]] = {
    ctx.transaction { implicit ec =>
      userProfileDao.update(profile).flatMap {
        case false => Future.successful(None)
        case true => userDao.getUserDetails(profile.userId)
      }
    }
  }
}
