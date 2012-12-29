package controllers

import play.api.mvc._

object ExampleController extends Controller {
  def index = Action {
    Ok("It works!")
  }
}