import definitions._
import scommons.sbtplugin.project.CommonModule

lazy val `scommons-admin` = (project in file("."))
  .settings(CommonModule.settings: _*)
  .settings(AdminModule.settings: _*)
  .settings(
    ideaExcludeFolders += s"${baseDirectory.value}/docs/_site"
  )
  .aggregate(
    `scommons-admin-client-api-jvm`,
    `scommons-admin-client-api-js`,
    `scommons-admin-client`,
    `scommons-admin-dao`,
    `scommons-admin-server`
)

lazy val `scommons-admin-client-api-jvm` = AdminClientApi.jvm
lazy val `scommons-admin-client-api-js` = AdminClientApi.js
lazy val `scommons-admin-client` = AdminClient.definition
lazy val `scommons-admin-dao` = AdminDao.definition
lazy val `scommons-admin-server` = AdminServer.definition
