import info.schleichardt.play2.basicauth.CredentialsFromConfCheck
import play.api.GlobalSettings
import play.api.mvc.{Handler, Action, Results, RequestHeader}
import play.api.mvc.Results.Unauthorized
import info.schleichardt.play2.basicauth.BasicAuth._

object Global extends GlobalSettings {
  override def onRouteRequest(request: RequestHeader) =
    requireBasicAuthentication(request, new CredentialsFromConfCheck) {
      super.onRouteRequest(request)
    }
}
