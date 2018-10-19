package scommons.admin.server

import javax.inject.Inject

import controllers.AssetsFinder
import play.api.mvc._
import scommons.play.controllers.BasePageController

class AdminController @Inject() (components: ControllerComponents, finder: AssetsFinder)
  extends BasePageController(components, finder, "/scommons-admin", "scommons-admin-client")
