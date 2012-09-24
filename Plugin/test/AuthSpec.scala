package info.schleichardt.play2.basicauth

import basicauth.{CredentialsFromConfCheck, Credentials, CredentialCheck}
import info.schleichardt.play2.basicauth._
import info.schleichardt.play2.basicauth.BasicAuth._
import org.specs2.mutable._
import play.api.mvc.{SimpleResult, Handler, RequestHeader, Action}
import play.api.mvc.Results.Unauthorized
import play.api.test.{FakeApplication, FakeRequest, FakeHeaders}
import play.api.test._
import play.api.test.Helpers._


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
  val credentials1 = Credentials("name", "pw")

  "BasicAuth" should {
    "be able to encode credentials" in {
      BasicAuth.encodeCredentials(credentials1) === "bmFtZTpwdw=="
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
      }
      result.asInstanceOf[SimpleResult[Nothing]].header.status === Unauthorized.header.status
    }

    "be able to hash credentials" in {
      val configMap: Map[String, String] = Map("application.secret" -> "[SAH]^9=]9>cE]Sgq_5[=IEWAckN?87y?Pd8vG6mk:35b@X[M?c8735y5ew7uMja")
      val app: FakeApplication = FakeApplication(additionalConfiguration = configMap)
      running(app) {
        val hashed = BasicAuth.hashCredentialsWithApplicationSecret(credentials1)
        hashed === "5c83e7cdd87c8c3ac6000f53f2a0661ec346ffd5"
      }
    }
  }
}

class CredentialsFromConfCheckSpec extends Specification {
  def createApp() = {
    val userName = "name"
    val configMap = Map("application.secret" -> "[SAH]^9=]9>cE]Sgq_5[=IEWAckN?87y?Pd8vG6mk:35b@X[M?c8735y5ew7uMja",
      "basic.auth." + userName -> "5c83e7cdd87c8c3ac6000f53f2a0661ec346ffd5")
    FakeApplication(additionalConfiguration = configMap)
  }


  "Credentials from config file" should {
    "be confirmed, if existing" in {
      running(createApp()) {
        new CredentialsFromConfCheck().authorized(Option(Credentials("name", "pw"))) === true
      }
    }

    "be denied, if not existing" in {
      running(createApp()) {
        new CredentialsFromConfCheck().authorized(Option(Credentials("name", "NOTpw"))) === false
      }
    }
  }
}