package akka.libprocess

import java.net.{ InetSocketAddress, ServerSocket }

import akka.actor.{ ActorSystem, Props }
import akka.libprocess.LibProcessManager.RemoteRef
import akka.libprocess.serde.{ RawMessageSerDe, TransportMessage }
import akka.testkit.{ TestProbe, TestActorRef, TestKit }
import akka.util.ByteString
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

class LibProcessRemoteActorSpec extends TestKit(ActorSystem("system")) with WordSpecLike with Matchers with BeforeAndAfterAll {

  import system.dispatcher

  override def afterAll(): Unit = {
    system.shutdown()
  }

  "A LibProcessRemoteActorSpec" should {
    "connect to the remote endpoint" in {
      val socket = new ServerSocket()
      socket.bind(new InetSocketAddress("127.0.0.1", 0))
      val sockFuture = Future(socket.accept())
      val probe = TestProbe()

      val testRef = TestActorRef[LibProcessRemoteActor](
        Props(
          classOf[LibProcessRemoteActor],
          Some(probe.ref),
          "test",
          new InetSocketAddress(socket.getInetAddress, socket.getLocalPort),
          new InetSocketAddress("127.0.0.1", 5051),
          new RawMessageSerDe
        ),
        "ConnectionTestActor"
      )

      probe.expectMsg(RemoteRef(testRef))
      val remoteSocket = Await.result(sockFuture, 1.second)

      try {
        remoteSocket.isConnected should be(true)
      } finally {
        remoteSocket.close()
        socket.close()
        system.stop(testRef)
      }
    }

    "send serialized messages over the wire" in {
      val socket = new ServerSocket()
      socket.bind(new InetSocketAddress("127.0.0.1", 0))
      val sockFuture = Future(socket.accept())

      val testRef = TestActorRef[LibProcessRemoteActor](
        Props(
          classOf[LibProcessRemoteActor],
          None,
          "test",
          new InetSocketAddress(socket.getInetAddress, socket.getLocalPort),
          new InetSocketAddress("127.0.0.1", 5051),
          new RawMessageSerDe
        ),
        "SerializingTestActor"
      )

      val remoteSocket = Await.result(sockFuture, 1.second)

      testRef ! LibProcessMessage("slave", "Some string")
      try {
        val expectedHeaders =
          ("POST /test/java.lang.String HTTP/1.1\r\n" +
            "Libprocess-From: slave@127.0.0.1:5051\r\n" +
            "Connection: Keep-Alive\r\n" +
            "Content-Length: 18\r\n" +
            "Host: 127.0.0.1").getBytes.toSeq

        val buffer = Array.ofDim[Byte](1024)
        val bytesRead = remoteSocket.getInputStream.read(buffer)
        val resized = buffer.take(bytesRead)
        val headers = resized.take(expectedHeaders.size)
        val body = resized.drop(expectedHeaders.size + 4)

        headers.toSeq should equal(expectedHeaders)
        val deserializedBody = new RawMessageSerDe().deserialize(TransportMessage("", ByteString(body)))
        deserializedBody.isSuccess should be(true)
        deserializedBody.get should be("Some string")
      } finally {
        remoteSocket.close()
        socket.close()
        system.stop(testRef)
      }
    }
  }
}
