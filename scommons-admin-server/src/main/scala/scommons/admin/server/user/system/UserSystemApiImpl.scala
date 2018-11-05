package scommons.admin.server.user.system

import scommons.admin.client.api.AdminUiApiStatuses._
import scommons.admin.client.api.user.system._
import scommons.admin.domain.{Company, SystemEntity, User}
import scommons.admin.server.user.system.UserSystemApiImpl._
import scommons.admin.server.user.{UserApiImpl, UserService}

import scala.concurrent.{ExecutionContext, Future}

class UserSystemApiImpl(userService: UserService,
                        userSystemService: UserSystemService)(implicit ec: ExecutionContext)
  extends UserSystemApi {

  def listUserSystems(userId: Int): Future[UserSystemResp] = {
    userService.getUserWithCompany(userId).flatMap {
      case None => Future.successful(UserSystemResp(UserNotFound))
      case Some((user, company)) =>
        userSystemService.listUserSystems(user).map { systems =>
          convertToUserSystemResp(systems, user, company)
        }
    }
  }

  def addUserSystems(userId: Int, data: UserSystemUpdateReq): Future[UserSystemResp] = {
    userService.getUserWithCompany(userId).flatMap {
      case None => Future.successful(UserSystemResp(UserNotFound))
      case Some((user, company)) =>
        (for {
          systems <- userSystemService.addUserSystems(
            user.copy(version = data.version),
            data.systemIds
          )
          updatedUser <- userService.getUserById(userId)
        } yield {
          systems.flatMap(sx => updatedUser.map(r => (sx, r)))
        }).map {
          case None => UserSystemResp(UserAlreadyUpdated)
          case Some((systems, updatedUser)) =>
            convertToUserSystemResp(systems, updatedUser, company)
        }
    }
  }

  def removeUserSystems(userId: Int, data: UserSystemUpdateReq): Future[UserSystemResp] = {
    userService.getUserWithCompany(userId).flatMap {
      case None => Future.successful(UserSystemResp(UserNotFound))
      case Some((user, company)) =>
        (for {
          systems <- userSystemService.removeUserSystems(
            user.copy(version = data.version),
            data.systemIds
          )
          updatedUser <- userService.getUserById(userId)
        } yield {
          systems.flatMap(sx => updatedUser.map(r => (sx, r)))
        }).map {
          case None => UserSystemResp(UserAlreadyUpdated)
          case Some((systems, updatedUser)) =>
            convertToUserSystemResp(systems, updatedUser, company)
        }
    }
  }
}

object UserSystemApiImpl {

  def convertToUserSystemResp(systems: List[(SystemEntity, Boolean)],
                              user: User,
                              company: Company): UserSystemResp = {

    UserSystemResp(UserSystemRespData(
      systems = systems.map { case (p, isEnabled) =>
        convertToUserSystemData(p, isEnabled)
      },
      user = UserApiImpl.convertToUserData(user, company)
    ))
  }
  
  def convertToUserSystemData(s: SystemEntity, isSelected: Boolean): UserSystemData = {
    UserSystemData(
      id = s.id,
      name = s.name,
      isSelected = isSelected
    )
  }
}
