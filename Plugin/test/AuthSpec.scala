import info.schleichardt.play2.basicauth.{CredentialCheck, BasicAuth, Credentials}
import info.schleichardt.play2.basicauth.BasicAuth._
import org.specs2.mutable._
import play.api.mvc.{SimpleResult, Handler, RequestHeader, Action}
import play.api.mvc.Results.Unauthorized
import play.api.test.{FakeRequest, FakeHeaders}
class TestCredentialChecker extends CredentialCheck {


  def authorized(credentials: Option[Credentials]) = credentials match {
    case Some(Credentials("User 1", "Password 1")) | Some(Credentials("name", "pw")) => true
    case _ => false
  }
}

class AuthSpec extends Specification {
  val emptyCredentialsHeader = Option("Basic Og==")
  val credentialsWithOnlyUserNameHeader = Option("Basic bmFtZTo=")
  val credentialsWithOnlyPasswordHeader = Option("Basic OnB3")
  val credentialsHeader = Option("Basic bmFtZTpwdw==")

  "BasicAuth" should {
    "be able to encode credentials" in {
      val credentials = Credentials("name", "pw")
      val expected = "bmFtZTpwdw=="
      BasicAuth.encodeCredentials(credentials) === expected
    }

    "be able to extract credentials from a request" in {
      "request with empty credentials" in {
        extractAuthDataFromHeader(emptyCredentialsHeader) === None
      }

      "request with only a userName" in {
        extractAuthDataFromHeader(credentialsWithOnlyUserNameHeader) === None
      }

      "request with only a password" in {
        extractAuthDataFromHeader(credentialsWithOnlyPasswordHeader) === None
      }

      "request with userName and password" in {
        extractAuthDataFromHeader(credentialsHeader) === Option(Credentials("name", "pw"))
      }
    }

    "be able to verify correct credentials" in {
      requireBasicAuthentication(credentialsHeader, new TestCredentialChecker, "Message")(None) === None
    }

    "be able to reject wrong credentials" in {
      val result = requireBasicAuthentication(credentialsWithOnlyUserNameHeader, new TestCredentialChecker, "Message")(None).get match {
        case action: Action[_] => action.apply(play.api.test.FakeRequest.apply().asInstanceOf[play.api.mvc.Request[Nothing]])
        case _ => throw new RuntimeException("unexpected type")
      }

      result.asInstanceOf[SimpleResult[Nothing]].header.status === Unauthorized.header.status
    }
  }
}
