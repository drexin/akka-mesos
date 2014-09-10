package akka.libprocess

import org.scalatest.{ Matchers, WordSpec }

class PIDSpec extends WordSpec with Matchers {
  "A PID" should {
    "be correctly parsed" in {
      val pid = PID.fromAddressString("master@127.0.0.1:5050").get
      pid.id should be("master")
      pid.ip should be("127.0.0.1")
      pid.port should be(5050)
    }

    "fail to parse invalid PID" in {
      val pid = PID.fromAddressString("invalid")
      pid.isFailure should be(true)
      intercept[InvalidPIDException] {
        pid.get
      }
    }

    "be correctly stringified" in {
      val pid = PID("127.0.0.1", 5050, "master")
      pid.toAddressString should be("master@127.0.0.1:5050")
    }
  }
}
