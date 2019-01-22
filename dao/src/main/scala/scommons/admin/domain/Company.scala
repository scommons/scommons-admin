package scommons.admin.domain

case class Company(id: Int, name: String)

//noinspection TypeAnnotation
trait CompanySchema {

  val ctx: AdminDBContext
  import ctx._

  implicit val categoriesInsertMeta = insertMeta[Company](
    _.id
  )
  implicit val categoriesUpdateMeta = updateMeta[Company](
    _.id
  )

  val companies = quote {
    querySchema[Company](
      "companies",
      _.id -> "id",
      _.name -> "name"
    )
  }
}
