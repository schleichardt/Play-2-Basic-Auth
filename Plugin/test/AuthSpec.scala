import info.schleichardt.play2.basicauth.BasicAuth
import info.schleichardt.play2.basicauth.BasicAuth._
import info.schleichardt.play2.basicauth.Credentials
import org.specs2.mutable._

class AuthSpec extends Specification {
  "BasicAuth" should {
    "be able to encode credentials" in {
      val credentials = Credentials("name", "pw")
      val expected = "bmFtZTpwdw=="
      BasicAuth.encodeCredentials(credentials) === expected
    }

    "be able to extract credentials from a request" in {
      "request with empty credentials" in {
        val emptyCredentialsHeader = Option("Basic Og==")
        extractAuthDataFromHeader(emptyCredentialsHeader) === None
      }

      "request with only a userName" in {
        val credentialsWithOnlyUserNameHeader = Option("Basic bmFtZTo=")
        extractAuthDataFromHeader(credentialsWithOnlyUserNameHeader) === None
      }

      "request with only a password" in {
        val credentialsWithOnlyPasswordHeader = Option("Basic OnB3")
        extractAuthDataFromHeader(credentialsWithOnlyPasswordHeader) === None
      }

      "request with userName and password" in {
        val credentialsHeader = Option("Basic bmFtZTpwdw==")
        extractAuthDataFromHeader(credentialsHeader) === Option(Credentials("name", "pw"))
      }
    }
  }
}
