package info.schleichardt.play2.basicauth

import play.api.mvc.{Action, Handler, RequestHeader}
import play.api.mvc.Results._
import play.api.Play
import org.springframework.security.crypto.bcrypt.BCrypt
import play.api.libs.Crypto


case class Credentials(userName: String, password: String)

trait CredentialCheck {
  def authorized(credentials: Option[Credentials]): Boolean
}

object BasicAuth {
  def encodeCredentials(credentials: Credentials): String = {
    val formatted = credentials.userName + ":" + credentials.password
    new String(org.apache.commons.codec.binary.Base64.encodeBase64(formatted.getBytes))
  }

  def extractAuthDataFromHeader(headerOption: Option[String]): Option[Credentials] = {
    //inspired from guillaumebort  https://gist.github.com/2328236 24.09.2012
    headerOption.flatMap {
      authorization =>
        authorization.split(" ").drop(1).headOption.flatMap {
          encoded =>
            new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes)).split(":").toList match {
              case userName :: password :: Nil if userName.length > 0 && password.length > 0 => Option(Credentials(userName, password))
              case _ => None
            }
        }
    }
  }

  def requireBasicAuthentication(request: RequestHeader, checker: CredentialCheck, message: String = "Authentication needed")(handler: => Option[Handler]): Option[Handler] = {
    val authHeader = request.headers.get("Authorization")
    requireBasicAuthentication(authHeader, checker: CredentialCheck, message)(handler)
  }

  def requireBasicAuthentication(authHeader: Option[String], checker: CredentialCheck, message: String)(handler: => Option[Handler]): Option[Handler] = {
    val mayBeCredentials = extractAuthDataFromHeader(authHeader)
    if (checker.authorized(mayBeCredentials))
      handler
    else
      Option(Action {
        Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="%s"""".format(message))
      })
  }

  def hashCredentialsWithApplicationSecret(credentials: Credentials): String = {
    Crypto.sign(encodeCredentials(credentials))
  }
}
