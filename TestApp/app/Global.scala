import play.api.GlobalSettings

object Global extends GlobalSettings {

  val requireBasicAuthentication = Authenticator(new CredentialsFromConfChecker)

  override def onRouteRequest(request: RequestHeader) = requireBasicAuthentication(request, super.onRouteRequest(request))
}
