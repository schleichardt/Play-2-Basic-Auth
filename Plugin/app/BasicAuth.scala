package info.schleichardt.play2.basicauth

import play.api.mvc.Request

case class Credentials(userName: String, password: String)

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


}
