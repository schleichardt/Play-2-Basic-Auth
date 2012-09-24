package info.schleichardt.play2.basicauth

case class Credentials(userName: String, password: String)

object BasicAuth {
  def encodeCredentials(credentials: Credentials): String = {
    val formatted = credentials.userName + ":" + credentials.password
    new String(org.apache.commons.codec.binary.Base64.encodeBase64(formatted.getBytes))
  }
}
