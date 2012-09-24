import info.schleichardt.play2.basicauth.BasicAuth
import info.schleichardt.play2.basicauth.Credentials
import org.specs2.mutable._

import play.api.test._
import play.api.test.FakeApplication
import play.api.test.Helpers._

import scala.collection.JavaConversions._

class AuthSpec extends Specification {
  "BasicAuth" should {
    "be able to encode credentials" in {
      val credentials = Credentials("name", "pw")
      val expected = "bmFtZTpwdw=="
      BasicAuth.encodeCredentials(credentials) === expected
    }
  }
}
