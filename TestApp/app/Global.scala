import play.api.GlobalSettings
import play.api.mvc.RequestHeader
import info.schleichardt.play2.api.basicauth.CredentialsFromConfChecker
import info.schleichardt.play2.api.basicauth.Authenticator

object Global extends GlobalSettings {

  val requireBasicAuthentication = Authenticator(new CredentialsFromConfChecker)

  override def onRouteRequest(request: RequestHeader) = requireBasicAuthentication(request, () => super.onRouteRequest(request))
}
