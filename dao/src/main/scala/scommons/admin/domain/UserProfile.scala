package scommons.admin.domain

import org.joda.time.{DateTime, DateTimeZone}

case class UserProfile(userId: Int,
                       email: String,
                       firstName: String,
                       lastName: String,
                       phone: Option[String],
                       updatedBy: Int,
                       updatedAt: DateTime = UserProfile.defaultDateTime,
                       createdAt: DateTime = UserProfile.defaultDateTime,
                       version: Int = 1)

object UserProfile {
  private val defaultDateTime: DateTime = new DateTime(0, DateTimeZone.UTC)
}

//noinspection TypeAnnotation
trait UserProfileSchema {

  val ctx: AdminDBContext
  import ctx._

  implicit val usersProfilesInsertMeta = insertMeta[UserProfile](
    _.version, _.createdAt, _.updatedAt
  )
  implicit val usersProfilesUpdateMeta = updateMeta[UserProfile](
    _.version, _.createdAt, _.updatedAt
  )

  val usersProfiles = quote {
    querySchema[UserProfile](
      "users_profiles",
      _.userId -> "user_id",
      _.email -> "email",
      _.firstName -> "first_name",
      _.lastName -> "last_name",
      _.phone -> "phone",
      _.updatedBy -> "updated_by",
      _.updatedAt -> "updated_at",
      _.createdAt -> "created_at",
      _.version -> "version"
    )
  }
}
