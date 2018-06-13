import common.Common
import definitions._

lazy val `scommons-admin` = (project in file("."))
  .settings(Common.settings)
  .settings(
    ideaExcludeFolders += s"${baseDirectory.value}/docs/_site"
  )
  .aggregate(
    `scommons-admin-client-api-jvm`,
    `scommons-admin-client-api-js`,
    `scommons-admin-client`,
    `scommons-admin-server`
)

lazy val `scommons-admin-client-api-jvm` = AdminClientApi.jvm
lazy val `scommons-admin-client-api-js` = AdminClientApi.js
lazy val `scommons-admin-client` = AdminClient.definition
lazy val `scommons-admin-server` = AdminServer.definition
