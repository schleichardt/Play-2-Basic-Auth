package info.schleichardt.play2.basicauth

import info.schleichardt.play2.api.basicauth.BasicAuth._
import org.specs2.mutable._
import play.api.test.Helpers._
import info.schleichardt.play2.api.basicauth.{CredentialsFromConfChecker, Authenticator, CredentialChecker, Credentials}
import play.api.test.FakeApplication


object TestCredentialChecker extends CredentialChecker {
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
  val requireBasicAuthentication = Authenticator(TestCredentialChecker)

  "BasicAuth" should {
    "be able to encode credentials" in {
      encodeCredentials(credentials1) === "bmFtZTpwdw=="
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

    "be able to hash credentials" in {
      val configMap: Map[String, String] = Map("application.secret" -> "[SAH]^9=]9>cE]Sgq_5[=IEWAckN?87y?Pd8vG6mk:35b@X[M?c8735y5ew7uMja")
      val app: FakeApplication = FakeApplication(additionalConfiguration = configMap)
      running(app) {
        val hashed = CredentialsFromConfChecker.hashCredentialsWithApplicationSecret(credentials1)
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
        new CredentialsFromConfChecker().authorized(Option(Credentials("name", "pw"))) === true
      }
    }

    "be denied, if not existing" in {
      running(createApp()) {
        new CredentialsFromConfChecker().authorized(Option(Credentials("name", "NOTpw"))) === false
      }
    }
  }
}