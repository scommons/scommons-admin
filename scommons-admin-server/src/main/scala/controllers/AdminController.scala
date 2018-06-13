package controllers

import javax.inject.Inject

import play.api.mvc._
import scommons.play.controllers.BasePageController

class AdminController @Inject() (components: ControllerComponents, finder: AssetsFinder)
  extends BasePageController(components, finder, "/scommons-admin", "scommons-admin-client")
