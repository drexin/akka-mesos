package akka.libprocess

import akka.actor.{ ActorSystem, Props }
import akka.libprocess.LibProcessManager._
import akka.testkit.{ ImplicitSender, TestKit, TestProbe }
import com.typesafe.config.ConfigFactory
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import scala.concurrent.duration._

class LibProcessManagerSpec extends TestKit(ActorSystem("system")) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  override def afterAll(): Unit = {
    system.shutdown()
  }

  "A LibProcessManager" should {
    "allow actors to register" in {
      val probe = TestProbe()
      val config = ConfigFactory.load()
      val manager = system.actorOf(
        Props(
          classOf[LibProcessManager],
          new LibProcessConfig(config.getConfig("akka.libprocess"))
        )
      )
      manager ! Register("test", probe.ref)
      expectMsg(1.seconds, Registered("test"))
    }

    "not allow to register a name twice" in {
      val probe = TestProbe()
      val config = ConfigFactory.load()
      val manager = system.actorOf(
        Props(
          classOf[LibProcessManager],
          new LibProcessConfig(config.getConfig("akka.libprocess"))
        )
      )
      manager ! Register("test", probe.ref)
      expectMsg(1.seconds, Registered("test"))

      manager ! Register("test", probe.ref)
      expectMsg(1.seconds, RegistrationFailed("test"))
    }

    "allow actors to unregister" in {
      val probe = TestProbe()
      val config = ConfigFactory.load()
      val manager = system.actorOf(
        Props(
          classOf[LibProcessManager],
          new LibProcessConfig(config.getConfig("akka.libprocess"))
        )
      )
      manager ! Register("test", probe.ref)
      expectMsg(1.seconds, Registered("test"))

      manager ! Unregister("test")
      expectMsg(Unregistered("test"))

      // check if we really unregistered
      manager ! Register("test", probe.ref)
      expectMsg(1.seconds, Registered("test"))
    }

    "deliver messages to the registered actor" in {
      val probe = TestProbe()
      val config = ConfigFactory.load()
      val manager = system.actorOf(
        Props(
          classOf[LibProcessManager],
          new LibProcessConfig(config.getConfig("akka.libprocess"))
        )
      )
      manager ! Register("test", probe.ref)
      expectMsg(1.seconds, Registered("test"))

      manager ! LibProcessInternalMessage(PID("127.0.0.1", 5050, "master"), "test", "Some message")

      probe.expectMsg("Some message")
    }
  }
}
