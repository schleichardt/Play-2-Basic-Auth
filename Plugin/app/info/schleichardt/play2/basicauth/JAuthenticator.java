package info.schleichardt.play2.basicauth;

import info.schleichardt.play2.basicauth.BasicAuth$;
import info.schleichardt.play2.basicauth.CredentialChecker;
import info.schleichardt.play2.basicauth.Credentials;
import play.api.mvc.Handler;
import play.mvc.Http;
import scala.Option;

public class JAuthenticator {
    private final CredentialChecker credentialChecker;

    public JAuthenticator(final CredentialChecker credentialChecker) {
        this.credentialChecker = credentialChecker;
    }

    public Handler requireBasicAuthentication(final Http.RequestHeader requestHeader, final Handler defaultHandler) {
        final Option<String> maybeHeader = Option.apply(requestHeader.getHeader("Authorization"));
        final Option<Credentials> credentialsOption = BasicAuth$.MODULE$.extractAuthDataFromHeader(maybeHeader);
        if(credentialChecker.authorized(credentialsOption)) {
            return defaultHandler;
        } else {
            return BasicAuth$.MODULE$.unauthorizedHandlerOption("Authentication needed").get();
        }
    }
}
