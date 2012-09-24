import info.schleichardt.play2.basicauth.CredentialsFromConfCheck
import play.api.GlobalSettings
import play.api.mvc.{Handler, Action, Results, RequestHeader}
import play.api.mvc.Results.Unauthorized
import info.schleichardt.play2.basicauth._

object Global extends GlobalSettings {

  val credentialSource = new CredentialsFromConfCheck

  override def onRouteRequest(request: RequestHeader) =
    requireBasicAuthentication(request, credentialSource) {
      super.onRouteRequest(request)
    }
}
