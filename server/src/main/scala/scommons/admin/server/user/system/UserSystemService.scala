package scommons.admin.server.user.system

import scommons.admin.domain.dao._
import scommons.admin.domain._

import scala.concurrent.{ExecutionContext, Future}

class UserSystemService(userDao: UserDao,
                        systemDao: SystemDao,
                        systemUserDao: SystemUserDao)(implicit ec: ExecutionContext) {

  import userDao.ctx

  def listUserSystems(user: User): Future[List[(SystemEntity, Boolean)]] = {
    systemDao.listUserSystems(user.id)
  }
  
  def addUserSystems(user: User, systemIds: Set[Int]): Future[Option[List[(SystemEntity, Boolean)]]] = {
    ctx.transaction { implicit ec: ExecutionContext =>
      for {
        currSystems <- systemDao.listUserSystems(user.id)
        userSystems = currSystems.collect {
          case (s, false) if systemIds.contains(s.id) =>
            SystemUser(s.id, user.id, updatedBy = user.updatedBy.get)
        }
        updated <- userDao.update(user)
        _ <- systemUserDao.insert(userSystems)
        res <-
          if (updated) systemDao.listUserSystems(user.id).map(Some(_))
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
  
  def removeUserSystems(user: User, systemIds: Set[Int]): Future[Option[List[(SystemEntity, Boolean)]]] = {
    ctx.transaction { implicit ec: ExecutionContext =>
      for {
        updated <- userDao.update(user)
        _ <- systemUserDao.delete(user.id, systemIds)
        res <-
          if (updated) systemDao.listUserSystems(user.id).map(Some(_))
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
}
