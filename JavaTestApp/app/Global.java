import info.schleichardt.play2.basicauth.CredentialsFromConfChecker;
import play.GlobalSettings;
import play.api.mvc.Handler;
import play.mvc.Http;
import info.schleichardt.play2.basicauth.JAuthenticator;

public class Global extends GlobalSettings {
    private JAuthenticator authenticator = new JAuthenticator(new CredentialsFromConfChecker());

    @Override
    public Handler onRouteRequest(final Http.RequestHeader requestHeader) {
        final Handler defaultHandler = super.onRouteRequest(requestHeader);
        return authenticator.requireBasicAuthentication(requestHeader, defaultHandler);
    }
}
