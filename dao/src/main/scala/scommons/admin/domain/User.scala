package scommons.admin.domain

import org.joda.time.{DateTime, DateTimeZone}

case class User(id: Int,
                companyId: Int,
                login: String,
                passhash: String,
                active: Boolean,
                lastLoginDate: Option[DateTime],
                updatedBy: Option[Int],
                updatedAt: DateTime = User.defaultDateTime,
                createdAt: DateTime = User.defaultDateTime,
                version: Int = 1)

object User {
  private val defaultDateTime: DateTime = new DateTime(0, DateTimeZone.UTC)
}

//noinspection TypeAnnotation
trait UserSchema {

  val ctx: AdminDBContext
  import ctx._

  implicit val usersInsertMeta = insertMeta[User](
    _.id, _.version, _.createdAt, _.updatedAt
  )
  implicit val usersUpdateMeta = updateMeta[User](
    _.id, _.version, _.createdAt, _.updatedAt
  )

  val users = quote {
    querySchema[User](
      "users",
      _.id -> "id",
      _.companyId -> "company_id",
      _.login -> "login",
      _.passhash -> "passhash",
      _.active -> "active",
      _.lastLoginDate -> "last_login_date",
      _.updatedBy -> "updated_by",
      _.updatedAt -> "updated_at",
      _.createdAt -> "created_at",
      _.version -> "version"
    )
  }
}
